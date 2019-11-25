module Check where

import AbsBlah
import PrintBlah
import ErrM
import Lib

import Data.Maybe
import qualified Data.Map as Map
import Control.Monad.Reader
import Control.Monad.State
import Control.Monad.Except

-- -----------------------------------------------------------------------------
-- ---------------------- Check ------------------------------------------------
-- -----------------------------------------------------------------------------
runTypeCheck tree = runExceptT $ runReaderT (check tree) emptyTypeEnv

check :: Program -> CheckReader ()
check (Prog defList) = do
    res <- checkDefs defList
    env <- ask
    return ()

-- -----------------------------------------------------------------------------
-- ---------------------- Top Definitions --------------------------------------
-- -----------------------------------------------------------------------------
checkDefs :: [Def] -> CheckReader TypeEnv
checkDefs (d:ds) = do
    env <- checkDef d
    local (const env) $ checkDefs ds
checkDefs [] = ask

checkDef :: Def -> CheckReader TypeEnv
checkDef d = case d of
    DVar vd -> checkVarDecl vd
    DFun fd -> checkFunDef fd

-- -----------------------------------------------------------------------------
-- ---------------------- Expressions ------------------------------------------
-- -----------------------------------------------------------------------------
inferExpr :: Expr -> CheckReader LiftedType
inferExpr (EString _) = return $ LTypeT TString
inferExpr (EInt _) = return $ LTypeT TInt
inferExpr (ETrue) = return $ LTypeT TBool
inferExpr (EFalse) = return $ LTypeT TBool
inferExpr (EAcc aid access) = do
    let vname = getNameFromId aid
    case access of
        AccApp exprs access' -> checkFunApp vname exprs
        _ -> checkVarAccess vname access
inferExpr (EPIncr expr) = checkExprIs expr $ LTypeT TInt
inferExpr (EPDecr expr) = checkExprIs expr $ LTypeT TInt
inferExpr (EIncr expr) = checkExprIs expr $ LTypeT TInt
inferExpr (EDecr expr) = checkExprIs expr $ LTypeT TInt
inferExpr (ETimes expr expr') = do
    checkExprIs expr $ LTypeT TInt
    checkExprIs expr' $ LTypeT TInt
inferExpr (EDiv expr expr') = do
    checkExprIs expr $ LTypeT TInt
    checkExprIs expr' $ LTypeT TInt
inferExpr (EPlus expr expr') = do
    checkExprIs expr $ LTypeT TInt
    checkExprIs expr' $ LTypeT TInt
inferExpr (EMinus expr expr') = do
    checkExprIs expr $ LTypeT TInt
    checkExprIs expr' $ LTypeT TInt
inferExpr (ELt expr expr') = do
    checkExprIs expr $ LTypeT TInt
    checkExprIs expr' $ LTypeT TInt
    return $ LTypeT TBool
inferExpr (EGt expr expr') = do
    checkExprIs expr $ LTypeT TInt
    checkExprIs expr' $ LTypeT TInt
    return $ LTypeT TBool
inferExpr (ELtEq expr expr') = do
    checkExprIs expr $ LTypeT TInt
    checkExprIs expr' $ LTypeT TInt
    return $ LTypeT TBool
inferExpr (EGtEq expr expr') = do
    checkExprIs expr $ LTypeT TInt
    checkExprIs expr' $ LTypeT TInt
    return $ LTypeT TBool
inferExpr (EEq expr expr') = do
    checkExprTypesMatch expr expr'
    return $ LTypeT TBool
inferExpr (ENEq expr expr') = do
    checkExprTypesMatch expr expr'
    return $ LTypeT TBool
inferExpr (EAnd expr expr') = do
    checkExprIs expr $ LTypeT TBool
    checkExprIs expr' $ LTypeT TBool
inferExpr (EOr expr expr') = do
    checkExprIs expr $ LTypeT TBool
    checkExprIs expr' $ LTypeT TBool
inferExpr (ENot expr) = checkExprIs expr $ LTypeT TBool
inferExpr (EAss expr ass expr') = do
    lt <- inferExpr expr
    case ass of
        AssPure -> do
            checkExprTypesMatch expr expr'
        _ -> do
            checkExprIs expr $ LTypeT TInt
            checkExprIs expr' $ LTypeT TInt

inferExprList :: [Expr] -> CheckReader [LiftedType]
inferExprList (e:es) = do
    lt <- inferExpr e
    lts <- inferExprList es
    return $ [lt] ++ lts
inferExprList [] = return []

checkExprIs :: Expr -> LiftedType -> CheckReader LiftedType
checkExprIs expr mtype = do
    etype <- inferExpr expr
    if (etype == mtype) then
        return etype
    else
        lift $ throwError ("Expression " ++ (printTree expr) ++ " must be " ++
            (show mtype) ++ ".")

checkExprTypesMatch :: Expr -> Expr -> CheckReader LiftedType
checkExprTypesMatch expr expr' = do
    etype <- inferExpr expr
    etype' <- inferExpr expr'
    if (etype == etype') then
        return etype
    else
        lift $ throwError ("Expressions type mismatch: expression " ++ printTree (expr) ++
            " has type " ++ show (etype) ++ " vs expression " ++ printTree (expr') ++
            " has type " ++ show (etype'))

checkFunApp :: FName -> [Expr] -> CheckReader LiftedType
checkFunApp fname es = do
    env <- ask
    case getFunType fname env of
        Nothing ->
            lift $ throwError ("Function name " ++ fname ++ " not in scope. ")
        Just funType -> do
            lts <- inferExprList es
            let fargs = funArgs funType
            mapM checkFunArgAppMatch $ zip lts fargs
            let rettype = (funRetType funType)
            case rettype of
                TFTypeT t -> return $ LTypeT t
                TFTypeF t -> return $ LTypeF t

checkFunArgAppMatch :: (LiftedType, LiftedType) -> CheckReader ()
checkFunArgAppMatch (LTypeT farg_type, LTypeT fparam_type) =
    checkFunArgAppMatchType farg_type fparam_type
checkFunArgAppMatch (LTypeF farg_ftype, LTypeF fparam_ftype) =
    checkFunArgAppMatchFType farg_ftype fparam_ftype
checkFunArgAppMatch _ =
    lift $ throwError ("Function arguments application type mismatch: non-function vs function type.")

checkFunArgAppMatchType :: Type -> Type -> CheckReader ()
checkFunArgAppMatchType farg_type fparam_type = do
    case (farg_type, fparam_type) of
        (TBool, TBool) -> return ()
        (TInt, TInt) -> return ()
        (TString, TString) -> return ()
        (TArray atype n, TArray ptype m) -> do
            if (n /= m) then
                lift $ throwError ("Function arguments application type mismatch for arrays: " ++
                    "array size " ++ (show n) ++ " vs " ++ (show m))
            else checkFunArgAppMatchType atype ptype
        (TRecord aid, TRecord pid) -> do
            let aname = getNameFromId aid
            let pname = getNameFromId pid
            if (aname == pname) then
                return ()
            else
                lift $ throwError ("Function arguments application type mismatch for records: " ++
                    aname ++ " vs " ++ pname)
        (TMap akeytype avaltype, TMap pkeytype pvaltype) -> do
            checkFunArgAppMatchType akeytype pkeytype
            checkFunArgAppMatchType avaltype pvaltype
        _ ->
            lift $ throwError ("Function arguments application type mismatch.")

checkFunArgAppMatchFType :: FType -> FType -> CheckReader ()
checkFunArgAppMatchFType farg_ftype fparam_ftype = do
    if (farg_ftype == fparam_ftype) then
        return ()
    else
        lift $ throwError ("Argument types mismatch in passing function as parameter: " ++
            (show farg_ftype) ++ " vs " ++ (show fparam_ftype))
-- -----------------------------------------------------------------------------
-- ---------------------- STATEMENTS -------------------------------------------
-- -----------------------------------------------------------------------------

checkStmt :: Stmt -> CheckReader TypeEnv
checkStmt (StmtVarDec vd) = checkVarDecl vd
checkStmt (StmtFunDef fd) = checkFunDef fd
checkStmt (SExpr expr) = do
    inferExpr expr
    ask
checkStmt (SEmpty) = ask
checkStmt (SWhile expr stmt) = do
    etype <- inferExpr expr
    case etype of
        LTypeT TBool ->
            checkStmt stmt
        _ ->
            lift $ throwError ("Non-bool condition in while loop.")
checkStmt (StmtIfelse expr stmt stmt') = do
    etype <- inferExpr expr
    case etype of
        LTypeT TBool -> do
            checkStmt stmt
            checkStmt stmt'
        _ ->
            lift $ throwError ("Non-bool condition in if-else statement.")
checkStmt (StmtIf expr stmt) = do
    etype <- inferExpr expr
    case etype of
        LTypeT TBool -> do
            checkStmt stmt
        _ ->
            lift $ throwError ("Non-bool condition in if statement.")
checkStmt (StmtPrint expr) = do
    inferExpr expr
    ask
checkStmt (SBlock bs) = do
    checkBlock bs

checkBlock :: [Stmt] -> CheckReader TypeEnv
checkBlock (s:ss) = do
    env <- checkStmt s
    local (const env) $ checkBlock ss
checkBlock [] = ask

-- -----------------------------------------------------------------------------
-- ---------------------- VARIABLE DECLARATIONS --------------------------------
-- -----------------------------------------------------------------------------

checkVarDecl :: VarDecl -> CheckReader TypeEnv
checkVarDecl (DVarSimple vtype vid) = do
    checkVarType vtype vid
checkVarDecl (DVarList vtype vid (vid':vids)) = checkVarDeclList vtype (vid:vid':vids)
checkVarDecl (DVarExpr vtype vid expr) = do
    let vname = getNameFromId vid
    etype <- inferExpr expr
    case etype of
        LTypeT ttype ->
            if (vtype == ttype) then
                checkVarType vtype vid
            else
                lift $ throwError ("Type mismatch: variable " ++ vname ++ " declared type is " ++
                (show vtype) ++ " vs expression type is " ++ (show ttype) ++ ".")
        _ ->
            lift $ throwError ("RHS expression is " ++ (show etype) ++ " so it seems t be a function or void. Cannot assign.")

checkVarDecl (DVarRecord vds rid) = do
    let rname = getNameFromId rid
    env <- ask
    if (Map.member rname (recTypeEnv env)) then do
        lift $ throwError ("Record '" ++ rname ++ "' redefined. ")
    else do
        env' <- registerRecordName rname
        local (const env') $ registerRecordFields rname vds

registerRecordName :: RName -> CheckReader TypeEnv
registerRecordName rname = do
    env <- ask
    return $ TypeEnv (varTypeEnv env) (funTypeEnv env) (Map.insert rname Map.empty (recTypeEnv env))

registerRecordFields :: RName -> [VarDecl] -> CheckReader TypeEnv
registerRecordFields rname (vd:vds) = do
    env <- registerRecordField rname vd
    local (const env) $ registerRecordFields rname vds

registerRecordFields _ [] = ask

registerRecordField :: RName -> VarDecl -> CheckReader TypeEnv
registerRecordField rname (DVarSimple vtype vid) = do
    let vname = getNameFromId vid
    case vtype of
        TRecord rid -> checkIfRecordNameRegistered . getNameFromId $ rid
        _ -> return ()
    env <- ask
    let rectype = (recTypeEnv env) Map.! rname
    if (Map.member vname rectype) then
        lift $ throwError $ "Field name '" ++ vname ++ "' already defined in record " ++
                rname ++ "."
    else do
        let rectype' = Map.insert vname vtype rectype
        return $ TypeEnv
                    ( varTypeEnv env )
                    ( funTypeEnv env )
                    ( Map.insert rname rectype' (recTypeEnv env) )

registerRecordField rname (DVarList vtype vid vids) =
    registerRecordFieldList rname vtype (vid:vids)

registerRecordField rname (DVarExpr vtype vid expr) = do
    etype <- inferExpr expr
    case etype of
        LTypeF _ -> lift $ throwError ("In record '" ++ rname ++ "': function type not allowed in records.")
        LTypeT ettype -> do
            if (ettype == vtype) then
                registerRecordField rname (DVarSimple vtype vid)
            else
                lift $ throwError ("In record " ++ rname ++ " field " ++
                    (show (getNameFromId vid)) ++ " declares type " ++ (show vtype) ++
                    " vs  RHS expr type is " ++ (show ettype) ++ ".")

registerRecordField rname (DVarRecord vds rid) = lift $ throwError
    ("Nested records declaration not allowed: " ++ (getNameFromId rid) ++
    " record declared inside record " ++ rname ++ ".")

registerRecordFieldList :: RName -> Type -> [Id] -> CheckReader TypeEnv
registerRecordFieldList rname vtype (vid:vids) = do
    env <- registerRecordField rname (DVarSimple vtype vid)
    local (const env) $ registerRecordFieldList rname vtype vids
registerRecordFieldList rname vtype [] = ask

checkIfRecordNameRegistered :: RName -> CheckReader ()
checkIfRecordNameRegistered rname = do
    env <- ask
    if (Map.member rname (recTypeEnv env)) then
        return ()
    else
        lift $ throwError ("Record " ++ rname ++ " unknown.")

checkMapKeyType :: Type -> CheckReader ()
checkMapKeyType TBool = return ()
checkMapKeyType TInt = return ()
checkMapKeyType TString = return ()
checkMapKeyType _ = lift $ throwError ("Map key type can only be primitve type.")

checkVarType :: Type -> Id -> CheckReader TypeEnv
checkVarType vtype vid = do
    env <- ask
    let vname = getNameFromId vid
    case vtype of
        TRecord rid ->
            checkIfRecordNameRegistered . getNameFromId $ rid
        TMap keytype valtype ->
            checkMapKeyType keytype
        _ ->
            return ()
    return $ env {varTypeEnv = Map.insert vname vtype (varTypeEnv env)}

checkArgType :: Type -> Id -> CheckReader TypeEnv
checkArgType atype aid = do
    env <- ask
    let aname = getNameFromId aid
    case atype of
        TRecord rid ->
            checkIfRecordNameRegistered . getNameFromId $ rid
        TMap keytype valtype ->
            checkMapKeyType keytype
        _ ->
            return ()
    return $ TypeEnv
                (Map.insert aname atype (varTypeEnv env))
                (funTypeEnv env)
                (recTypeEnv env)

registerVar :: VName -> Type -> CheckReader TypeEnv
registerVar vname vtype = do
    env <- ask
    return $ TypeEnv (Map.insert vname vtype (varTypeEnv env)) (funTypeEnv env) (recTypeEnv env)

checkVarDeclList :: Type -> [Id] -> CheckReader TypeEnv
checkVarDeclList vtype (vid:vids) = do
    env <- checkVarType vtype vid
    local (const env) $ checkVarDeclList vtype vids
checkVarDeclList varType [] = ask


-- -----------------------------------------------------------------------------
-- ---------------------- VARIABLE ACCESS --------------------------------------
-- -----------------------------------------------------------------------------

checkVarAccess :: VName -> Acc -> CheckReader LiftedType
checkVarAccess vname access = do
    env <- ask
    case (Map.lookup vname (varTypeEnv env)) of
        Nothing -> do
            case (Map.lookup vname (funTypeEnv env)) of
                Nothing -> lift $ throwError ("Name " ++ vname ++ " not defined neither as variable not as function name.")
                Just funtype -> do
                        case access of
                            AccEmpty -> return $ LTypeF (funRetType funtype)
                            _ -> lift $ throwError ("In passing function as parameter: wrong accessor ...")
        Just vtype -> do
            case access of
                AccEmpty -> return $ LTypeT vtype
                _ -> do
                    checkVarTypeAccess vtype access

checkVarTypeAccess :: Type -> Acc -> CheckReader LiftedType
checkVarTypeAccess t AccEmpty = return $ LTypeT t
checkVarTypeAccess (TRecord rid) access = checkRecordAccess rid access
checkVarTypeAccess (TArray arrtype n) access = checkArrayAccess arrtype access
checkVarTypeAccess (TMap keytype valtype) access = checkMapAccess keytype valtype access
checkVarTypeAccess primitive_type access = do
            lift $ throwError ("Cannot access a primitive type " ++
                (show primitive_type) ++ " with " ++ printTree (access) ++ ".")

checkRecordAccess :: Id -> Acc -> CheckReader LiftedType
checkRecordAccess rid (AccRec fieldid access) = do
    env <- ask
    let rname = getNameFromId rid
    let rectype = (recTypeEnv env) Map.! rname
    let fieldname = getNameFromId fieldid
    case (Map.lookup fieldname rectype) of
        Nothing ->
            lift $ throwError ("No field name '" ++ fieldname ++ "' for record " ++ rname ++ ".")
        Just fieldtype ->
            checkVarTypeAccess fieldtype access

checkArrayAccess arrtype (AccArray expr access) = do
    etype <- inferExpr expr
    case etype of
        LTypeT TInt ->
            checkVarTypeAccess arrtype access
        _ ->
            lift $ throwError ("Variable (array) access expression cannot be of " ++ (show etype))
checkArrayAccess arrtype acc = lift $ throwError ("Wrong accessor for array. ")

checkMapAccess :: Type -> Type -> Acc -> CheckReader LiftedType
checkMapAccess keytype valtype (AccMap e acc) = do
    etype <- inferExpr e
    case etype of
        LTypeF _ ->
            lift $ throwError ("Variable (map) access expression cannot be of " ++ (show etype))
        LTypeT ettype -> do
            if (keytype == ettype) then
                checkVarTypeAccess valtype acc
            else lift $ throwError ("Map key type mismatch: keytype is " ++ (show keytype) ++
                " vs access expression type is " ++ (show ettype) ++ ".")
checkMapAccess _ _ _ = lift $ throwError ("Wrong accessor for map.")

-- -----------------------------------------------------------------------------
-- ---------------------- FUNCTION DEFINITIONS ---------------------------------
-- -----------------------------------------------------------------------------

checkFunDef :: FunDef -> CheckReader TypeEnv
checkFunDef (DFunAll rettype fid args (DFunBody fstmts retstmt)) = do

    let fname = getNameFromId fid
    env <- ask
    let largs = map getArgType args
    let argnames = map getNameFromArg args
    if (not (unique argnames)) then
        lift $ throwError ("Function '" ++ fname ++ "': arguments list not unique.")
    else
        return ()
    env' <- local (const env) $ registerFunction fname largs rettype
    env'' <- local (const env') $ insertDirectFunArgs fname args
    let mftype = getFunType fname env''
    case mftype of
        Nothing -> lift $ throwError ("No fun type for " ++ fname ++ ". Strange ...")
        Just ftype -> do
            env''' <- local (const env'') $ checkFunBody fname ftype fstmts
            local (const env''') $ checkFunRetTypes fname retstmt rettype
            return env'

registerFunction :: FName -> [LiftedType] -> FType -> CheckReader TypeEnv
registerFunction fname largs rettype = do
    env <- ask
    if isJust (Map.lookup fname (funTypeEnv env)) then
        lift $ throwError ("Function name '" ++ fname ++ "' redefined.")
    else do
        let ftype = FunType rettype largs
        return $ TypeEnv
                    (varTypeEnv env)
                    (Map.insert fname ftype (funTypeEnv env))
                    (recTypeEnv env)

insertDirectFunArgs :: FName -> [Arg] -> CheckReader TypeEnv
insertDirectFunArgs fname (arg:args) = do
    env <- insertDirectFunArg fname arg
    local (const env) $ insertDirectFunArgs fname args
insertDirectFunArgs fname [] = ask

insertDirectFunArg :: FName -> Arg -> CheckReader TypeEnv
insertDirectFunArg fname arg = do
    env <- ask
    let aname = getNameFromArg arg
    let funtype = (funTypeEnv env) Map.! fname
    case arg of
        ArgFun (TFTypeT rettype) fid ftargs -> do
            let as = [ (getLTypeFromFType t) | t <- ftargs ]
            env' <- local (const env) $ registerFunction aname as (TFTypeT rettype)
            env'' <- local (const env') $ insertIndirectFunArgs aname ftargs
            return $ TypeEnv
                        (varTypeEnv env'')
                        (Map.insert fname (FunType (funRetType funtype) ((funArgs funtype) ++
                            [LTypeT rettype] ))(funTypeEnv env''))
                        (recTypeEnv env)
        ArgVal atype aid -> do
            env' <- checkArgType atype aid
            let env'' = TypeEnv
                        (varTypeEnv env')
                        (Map.insert fname (FunType (funRetType funtype)
                                                    ((funArgs funtype) ++ [LTypeT atype] ))
                                                    (funTypeEnv env'))
                        (recTypeEnv env)
            return env''
        ArgVar atype aid -> do
            if (isPrimitiveType atype) then do
                env' <- checkArgType atype aid
                let env'' = TypeEnv
                            (varTypeEnv env')
                            (Map.insert fname (FunType (funRetType funtype)
                                                        ((funArgs funtype) ++ [LTypeT atype] ))
                                                        (funTypeEnv env'))
                            (recTypeEnv env)
                return env''
            else
                lift $ throwError ("Passing argument explicitly by variable possible only for primitive types. ")

insertIndirectFunArgs :: FName -> [FType] -> CheckReader TypeEnv
insertIndirectFunArgs fname (a:as) = do
    env <- insertIndirectFunArg fname a
    local (const env) $ insertIndirectFunArgs fname as
insertIndirectFunArgs fname [] = ask

insertIndirectFunArg :: FName -> FType -> CheckReader TypeEnv
insertIndirectFunArg fname a = do
    env <- ask
    let funtype = (funTypeEnv env) Map.! fname
    case a of
        TFTypeT atype ->
            return $ TypeEnv
                        (varTypeEnv env)
                        (Map.insert fname (FunType (funRetType funtype) ((funArgs funtype) ++
                            [LTypeT atype] )) (funTypeEnv env))
                        (recTypeEnv env)
        TFTypeF rtype ->
            insertIndirectFunArg fname rtype

checkFunBody :: FName -> FunType -> [Stmt] -> CheckReader TypeEnv
checkFunBody fname funtype (stmt:stmts) = do
    env <- checkFunStmt fname funtype stmt
    local (const env) $ checkFunBody fname funtype stmts
checkFunBody fname funtype [] = ask

checkFunStmt :: FName -> FunType -> Stmt -> CheckReader TypeEnv
checkFunStmt fname funtype (StmtVarDec vd) = checkVarDecl vd
checkFunStmt fname funtype (StmtFunDef fd) = checkFunDef fd
checkFunStmt fname funtype (stmt) = do
    checkStmt stmt
    ask

checkFunRetTypes :: FName -> RetStmt -> FType -> CheckReader ()
checkFunRetTypes fname (StmtRetExpr expr) rettype = do
    etype <- inferExpr expr
    case rettype of
        TFTypeT rtype -> do
            case etype of
                LTypeT ttype ->
                    if (ttype == rtype) then
                        return ()
                    else
                        throwError $ "Function " ++ fname ++ " declares to return " ++
                            (show rtype) ++ " vs in body returning " ++ (show etype)
                _ ->
                    throwError $ "Function " ++ fname ++ " declares to return simple type  " ++
                    (show rtype) ++ " vs in body returning " ++ (show etype)
