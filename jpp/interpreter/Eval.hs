module Eval where

import AbsBlah
import PrintBlah
import ErrM
import Lib
import Check

import Data.Maybe
import qualified Data.Map as Map
import Control.Monad.Reader
import Control.Monad.State
import Control.Monad.Except

-- -----------------------------------------------------------------------------
-- ---------------------- Eval -------------------------------------------------
-- -----------------------------------------------------------------------------
runEval tree = runExceptT $ runStateT (runReaderT (eval tree) emptyEvalEnv ) emptyStore

eval :: Program -> EvalReader ()
eval (Prog defs) = do
    res <- evalDefs defs
    return ()

-- -----------------------------------------------------------------------------
-- ---------------------- Top Definitions --------------------------------------
-- -----------------------------------------------------------------------------
evalDefs :: [Def] -> EvalReader EvalEnv
evalDefs (d:ds) = do
    env <- evalDef d
    local (const env) $ evalDefs ds
evalDefs [] = do
    env <- ask
    return env

evalDef :: Def -> EvalReader EvalEnv
evalDef d = case d of
    DVar vd -> do
        (env, locs) <- evalVarDecl vd
        return env
    DFun fd -> evalFunDef fd

evalVarDecl :: VarDecl -> EvalReader (EvalEnv, [Loc])
evalVarDecl (DVarSimple vtype vid) = do
    let vname = getNameFromId vid
    env <- ask
    if (elem vname (idList env)) then
        lift $ throwError ("Name " ++ vname ++ " redifined.")
    else do
        store <- get
        initval <- initVal vtype
        newloc <- getNextLoc
        modify (Map.insert newloc (initval))
        return $ (env {vEnv = Map.insert vname newloc (vEnv env), idList = (idList env) ++ [vname]}, [newloc])
evalVarDecl (DVarExpr vtype vid expr) = do
    val <- evalExpr expr
    let vname = getNameFromId vid
    store <- get
    env <- ask
    newloc <- getNextLoc
    modify (Map.insert newloc val)
    return $ (env {vEnv = Map.insert vname newloc (vEnv env)}, [newloc])
evalVarDecl (DVarRecord fds recid) = do
    let rname = getNameFromId recid
    let fieldnames = getNamesFromVarDecls fds
    (env, locs) <- evalRecordFields fds
    let recmap = Map.fromList $ zip fieldnames locs
    return $ (env {rEnv = Map.insert rname recmap (rEnv env)}, [])
evalVarDecl (DVarList vtype vid vids) = do
    evalVarListDecl vtype (vid:vids)

evalVarListDecl :: Type -> [Id] -> EvalReader (EvalEnv, [Loc])
evalVarListDecl vtype (vid:vids) = do
    (env, locs) <- evalVarDecl (DVarSimple vtype vid)
    (env', locs') <- local (const env) $ evalVarListDecl vtype vids
    return (env', locs ++ locs')
evalVarListDecl vtype [] = do
    env <- ask
    return (env, [])

evalRecordFields :: [VarDecl] -> EvalReader (EvalEnv, [Loc])
evalRecordFields (fd:fds) = do
    (env, locs) <- evalVarDecl fd
    (env', locs') <- evalRecordFields fds
    return $ (env', locs ++ locs')
evalRecordFields [] = do
    env <- ask
    return (env, [])

evalFunDef :: FunDef -> EvalReader EvalEnv
evalFunDef (DFunAll rettype fid args body) = do
    env <- ask
    let fname = getNameFromId fid
    return $ env {fEnv = Map.insert fname ( Fun args body, (vEnv env) ) (fEnv env)}

-- -----------------------------------------------------------------------------
-- ---------------------- Expressions ------------------------------------------
-- -----------------------------------------------------------------------------

evalExpr :: Expr -> EvalReader Val
evalExpr (EString s) = return $ VString s
evalExpr (EInt i) = return $ VInt (fromInteger i)
evalExpr (ETrue) = return $ VBool True
evalExpr (EFalse) = return $ VBool False
-- x++
evalExpr (EPIncr expr) = do
    let expr' = EPlus expr (EInt 1)
    val <- evalExpr expr
    evalExpr (EAss expr AssPure expr')
    return $ val
-- x--
evalExpr (EPDecr expr) = do
    let expr' = EMinus expr (EInt 1)
    val <- evalExpr expr
    evalExpr (EAss expr AssPure expr')
    return $ val
-- ++x
evalExpr (EIncr expr) = do
    let expr' = EPlus expr (EInt 1)
    evalExpr (EAss expr AssPure expr')
-- --x
evalExpr (EDecr expr) = do
    let expr' = EMinus expr (EInt 1)
    evalExpr (EAss expr AssPure expr')
evalExpr (ETimes expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ mul val val'
evalExpr (EDiv expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    diva val val'
evalExpr (EPlus expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ add val val'
evalExpr (EMinus expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ sub val val'
evalExpr (ELt expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ lt val val'
evalExpr (EGt expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ gt val val'
evalExpr (ELtEq expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ lte val val'
evalExpr (EGtEq expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ gte val val'
evalExpr (EEq expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ VBool (val == val')
evalExpr (ENEq expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ VBool (val /= val')
evalExpr (EAnd expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ and' val val'
evalExpr (EOr expr expr') = do
    val <- evalExpr expr
    val' <- evalExpr expr'
    return $ or' val val'
evalExpr (ENot expr) = do
    val <- evalExpr expr
    return $ not' val
evalExpr (EAcc aid access) = do
    let aname = getNameFromId aid
    case access of
        -- function application
        AccApp exprs AccEmpty -> evalFun aname exprs
        -- variable access
        _ -> do
                env <- ask
                let mloc = Map.lookup aname (vEnv env)
                case mloc of
                    Nothing -> lift $ throwError ("No loc for " ++ aname ++ ". Strange ...")
                    Just loc -> do
                        store <- get
                        val <- gets (Map.! loc)
                        case (val, access) of

                            (VNull, _) ->
                                lift $ throwError ("Variable '" ++ aname ++
                                    "' is null, thus it cannot be right-value.")
                            (_, AccEmpty) ->
                                return val

                            (VMap m, AccMap expr acc') -> do
                                v <- evalExpr expr
                                if (Map.member v m) then do
                                    let loc' = (m Map.! v)
                                    val' <- gets (Map.! loc')
                                    return $ val'
                                else
                                    lift $ throwError ("When acessing '" ++ aname ++ "': value " ++
                                        (show v) ++ " not found.")
                            (VArray n m, AccArray expr acc') -> do
                                VInt v <- evalExpr expr
                                if (v >= 0 && v < n ) then
                                    if (Map.member v m) then do
                                        let loc' = (m Map.! v)
                                        val' <- gets (Map.! loc')
                                        return val'
                                    else
                                        lift $ throwError ("Value undefined for index " ++
                                            (show v))
                                else
                                    lift $ throwError ("Index out of bound for array: bound is" ++
                                        (show n) ++ " vs expression " ++ (printTree expr) )
evalExpr (EAss expr ass expr') = do
    rval <- evalExpr expr'
    case expr of
        EAcc vid access -> do
            let vname = getNameFromId vid
            env <- ask
            let loc = (vEnv env) Map.! vname
            assignValToLoc loc access rval
        _ -> lift $ throwError ("Expression " ++ (printTree expr) ++ "is non assignable.")

assignValToLoc :: Loc -> Acc -> Val -> EvalReader Val
assignValToLoc loc AccEmpty val = do
    modify (Map.adjust (const val) loc)
    return val
assignValToLoc arrloc (AccArray index_expr followup_access) rval = do
    (VInt index) <- evalExpr index_expr
    store <- get
    if (not $ Map.member arrloc store) then
        lift $ throwError ("Trying to access unallocated object (array context).")
    else do
        let x = store Map.! arrloc
        case x of
            VArray n arrmap -> do
                if (index >= n) then
                    lift $ throwError ("Index " ++ (show index) ++ " out of bound " ++
                        (show n) ++ " for array.")
                else do
                    if (Map.member index arrmap) then do
                        let loc = arrmap Map.! index
                        assignValToLoc loc followup_access rval
                        return rval
                    else do
                        newloc <- getNextLoc
                        modify (Map.insert newloc rval)
                        rval' <- assignValToLoc newloc followup_access rval
                        let arrmap' = Map.insert index newloc arrmap
                        modify (Map.insert arrloc (VArray n arrmap'))
                        return rval
            _ -> lift $ throwError ("Trying to access unallocated object (array context).")
assignValToLoc maploc (AccMap keyexpr followup_access) rval = do
    store <- get
    if (not $ Map.member maploc store) then
        lift $ throwError ("Trying to access unallocated object (map).")
    else do
        key <- evalExpr keyexpr
        let (VMap key_loc_map) = store Map.! maploc
        if (Map.member key key_loc_map) then do
            let key_loc = key_loc_map Map.! key
            assignValToLoc key_loc followup_access rval
            return rval
        else do
            newloc <- getNextLoc
            rval' <- assignValToLoc newloc followup_access rval
            modify (Map.insert newloc rval)
            let key_loc_map' = Map.insert key newloc key_loc_map
            modify (Map.insert maploc (VMap key_loc_map'))
            return rval
assignValToLoc recloc (AccRec field_id followup_access) rval = do
    store <- get
    if (not $ Map.member recloc store) then
        lift $ throwError ("Trying to access unallocated object / unstored location (record).")
    else do
        let (VRecord recmap) = store Map.! recloc
        let field_name = getNameFromId field_id
        let field_loc = recmap Map.! field_name
        assignValToLoc field_loc followup_access rval
        return rval

-- -----------------------------------------------------------------------------
-- ---------------------- Functions --------------------------------------------
-- -----------------------------------------------------------------------------
evalFun :: FName -> [Expr] -> EvalReader Val
evalFun fname exprs = do
    env <- ask
    let (fun, fun_venv) = ((fEnv env) Map.! fname)
    env' <- local (const (env {vEnv = fun_venv, idList = []})) $ evalFunArgs env (zip (fargs fun) exprs)
    (env'', ret) <- local (const env' ) $ (evalFunBody (fbody fun))
    return $ ret

evalFunArgs :: EvalEnv -> [(Arg, Expr)] -> EvalReader EvalEnv
evalFunArgs ext_env ((arg, expr) : xs) = do
    env <- evalFunArg ext_env (arg, expr)
    local (const env) $ evalFunArgs ext_env xs
evalFunArgs ext_env [] = ask

evalFunArg :: EvalEnv -> (Arg, Expr) -> EvalReader EvalEnv
evalFunArg ext_env (ArgVal atype aid, expr) = do
    -- local, primitive argument => get new loc and assign expr value to it
    if (isPrimitiveType atype) then do
        env <- ask
        -- eval expression value in external env
        val <- local (const ext_env) $ evalExpr expr
        let aname = getNameFromId aid
        newloc <- getNextLoc
        -- modify store
        modify (Map.insert newloc val)
        -- return new environment with local argument
        return $ env {vEnv = Map.insert aname newloc (vEnv env)}
    else do
        case expr of
            EAcc gid _ -> do
                -- update environment with local argument name, but use global var location
                let aname = getNameFromId aid
                let gname = getNameFromId gid
                loc <- getLocFromName gname
                env <- ask
                -- return $ (env {vEnv = Map.insert vname newloc (vEnv env)}, [newloc])
                return $ env {vEnv = Map.insert aname loc (vEnv env)}
            _ ->
                lift $ throwError ("In passing argument by variable only access"
                    ++ "expressions allowed. '" ++ (printTree expr) ++ "' not allowed." )
evalFunArg ext_env (ArgVar atype aid, expr) = do
    case expr of
        EAcc gid _ -> do
            -- update environment with local argument name, but use global var location
            let aname = getNameFromId aid
            let gname = getNameFromId gid
            loc <- getLocFromName gname
            env <- ask
            return $ env {vEnv = Map.insert aname loc (vEnv env)}
        _ ->
            lift $ throwError ("In passing argument by variable only access" ++
                "expressions allowed. '" ++ (printTree expr) ++ "' not allowed." )
evalFunArg ext_env (ArgFun rettype fid args, expr) = do
    let fname = getNameFromId fid
    case expr of
        (EAcc gid AccEmpty) -> do
            let gname = getNameFromId gid
            let (fun, venv) = (fEnv ext_env) Map.! gname
            env <- ask
            return $ env {fEnv = Map.insert fname (fun, venv) (fEnv env)}
        _ ->
            lift $ throwError ("Error in evaluating function arg being function ...")

evalFunBody :: FunBody -> EvalReader (EvalEnv, Val)
evalFunBody (DFunBody stmts retstmt) = do
    store <- get
    (env, val) <- evalStmts stmts
    local (const env) $ evalRetStmt retstmt

evalStmts :: [Stmt] -> EvalReader (EvalEnv, Val)
evalStmts (s:ss) = do
    (env, val) <- evalStmt s
    local (const env) $ evalStmts ss
evalStmts [] = do
    env <- ask
    return (env, VNull)

evalRetStmt :: RetStmt -> EvalReader (EvalEnv, Val)
evalRetStmt (StmtRetExpr expr) = do
    val <- evalExpr expr
    env <- ask
    return (env, val)

-- -----------------------------------------------------------------------------
-- ---------------------- Statements -------------------------------------------
-- -----------------------------------------------------------------------------

evalStmt :: Stmt -> EvalReader (EvalEnv, Val)
evalStmt (StmtVarDec vd) = do
    (env, _) <- evalVarDecl vd
    return (env, VNull)
evalStmt (StmtFunDef fd) = do
    env <- evalFunDef fd
    return (env, VNull)
evalStmt (SExpr expr) = do
    env <- ask
    val <- evalExpr expr
    return (env, val)
evalStmt (SEmpty) = do
    env <- ask
    return $ (env, VNull)
evalStmt (SBlock bss) = do
    env <- ask
    local (const (env {idList = []}) ) $ evalBlock bss
evalStmt (SWhile expr stmt) = do
    (VBool b) <- evalExpr expr
    if b then do
        (env, val) <- evalStmt stmt
        local (const env) $ evalStmt (SWhile expr stmt)
    else do
        env <- ask
        return (env, VNull)
evalStmt (StmtIfelse expr stmt stmt') = do
    (VBool b) <- evalExpr expr
    if b then
        evalStmt stmt
    else
        evalStmt stmt'
evalStmt (StmtIf expr stmt) = do
    (VBool b) <- evalExpr expr
    if b then
        evalStmt stmt
    else do
        env <- ask
        return $ (env, VNull)
evalStmt (StmtPrint expr) = do
    val <- evalExpr expr
    printValue val
    env <- ask
    return (env, VNull)

evalBlock :: [Stmt] -> EvalReader (EvalEnv, Val)
evalBlock (s:ss) = do
    (env, val) <- evalStmt s
    local (const env) $ evalBlock ss
evalBlock [] = do
    env <- ask
    return (env, VNull)

-- -----------------------------------------------------------------------------
-- ---------------------- Printing ---------------------------------------------
-- -----------------------------------------------------------------------------
prompt :: String
prompt = ">>> "

printValue :: Val -> EvalReader ()
printValue val = do
    s <- valToString val
    liftIO $ print (prompt ++ s)

valToString :: Val -> EvalReader String
valToString val = do
    case val of
        VNull -> return "null"
        VBool b -> return $ show b
        VInt i -> return $ show i
        VString s -> return s
        VArray n arrmap -> arrayToString n (Map.toList arrmap)
        VMap m -> mapToString (Map.toList m)
        VRecord rmap -> recordToString (Map.toList rmap)

recordToString :: [(RName, Loc)] -> EvalReader String
recordToString fields = do
    ss <- mapM recordFieldToString fields
    return $ "record : {" ++ (join ss) ++ "}"

recordFieldToString :: (RName, Loc) -> EvalReader String
recordFieldToString (name, loc) = do
    val <- gets (Map.! loc)
    valstr <- valToString val
    return $ " " ++ name ++ " = " ++ valstr ++ ", "

mapToString :: [(Val, Loc)] -> EvalReader String
mapToString elems = do
    ss <- mapM mapElemToString elems
    return $ "map : {" ++ (join ss) ++ "}"

mapElemToString :: (Val, Loc) -> EvalReader String
mapElemToString (key, loc) = do
    keystr <- valToString key
    val <- gets (Map.! loc)
    valstr <- valToString val
    return $ " (" ++ keystr ++ " -> " ++ valstr ++ ") "

arrayToString :: Int -> [(Int, Loc)] -> EvalReader String
arrayToString n elems = do
    ss <- mapM arrayElemToString elems
    return $ "array[" ++ (show n) ++ "] : <" ++ (join ss) ++ ">"

arrayElemToString :: (Int, Loc) -> EvalReader String
arrayElemToString (i, loc) = do
    val <- gets (Map.! loc)
    s <- valToString val
    return $ " (" ++ (show i) ++ ", " ++ s ++ ") "
