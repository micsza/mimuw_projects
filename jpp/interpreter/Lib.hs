module Lib where

import AbsBlah
import Data.Map as Map
import Data.List as List
import Control.Monad.Reader
import Control.Monad.State
import Control.Monad.Except

-- -----------------------------------------------------------------------------
-- ---------------------- CHECK ------------------------------------------------
-- -----------------------------------------------------------------------------

type Name = String
type VName = String
type FName = String
type AName = String
type RName = String

type Result = ExceptT String IO
type CheckReader a = ReaderT TypeEnv Result a

-- generalized type of both non-function types and function type
data LiftedType
    = LTypeT Type
    | LTypeF FType
        deriving (Show, Eq)

data TypeEnv = TypeEnv {
    varTypeEnv :: VarTypeEnv,
    funTypeEnv :: FunTypeEnv,
    recTypeEnv :: RecTypeEnv
}

data FunType = FunType {
    funRetType :: FType,
    funArgs :: [LiftedType]
} deriving (Show)

type VarTypeEnv = Map.Map VName Type
type FunTypeEnv = Map.Map FName FunType
type RecTypeEnv = Map.Map RName RecType
type RecType = Map.Map VName Type

typeIsMap :: Type -> Bool
typeIsMap vtype = case vtype of
    TMap _ _ -> True
    _ -> False

typeIsRecord :: Type -> Bool
typeIsRecord vtype = case vtype of
    TRecord _ -> True
    _ -> False

getNameFromId :: Id -> String
getNameFromId (Id x) = snd x

getArgType :: Arg -> LiftedType
getArgType arg = case arg of
    ArgVal atype _ -> LTypeT atype
    ArgFun atype _ _ -> LTypeF atype
    ArgVar atype _ -> LTypeT atype
    ArgInout atype _ -> LTypeT atype

getNameFromArg :: Arg -> Name
getNameFromArg = (getNameFromId . getIdFromArg)

getNamesFromVarDecl :: VarDecl -> [VName]
getNamesFromVarDecl (DVarSimple t vid) = [getNameFromId vid]
getNamesFromVarDecl (DVarExpr t vid expr) = [getNameFromId vid]
getNamesFromVarDecl (DVarRecord vds rid) = [getNameFromId rid]

getNamesFromVarDecls :: [VarDecl] -> [VName]
getNamesFromVarDecls vds = concat $ (List.map)   getNamesFromVarDecl vds

getIdFromArg :: Arg -> Id
getIdFromArg arg = case arg of
    ArgVal _ aid -> aid
    ArgFun _ aid _ -> aid
    ArgVar _ aid -> aid
    ArgInout _ aid -> aid

getFunType :: FName -> TypeEnv -> Maybe FunType
getFunType fname env = Map.lookup fname (funTypeEnv env)

getFunArgs :: FName -> TypeEnv -> Maybe [LiftedType]
getFunArgs fname env = fmap funArgs (getFunType fname env)

insertArg :: Arg -> FunType -> FunType
insertArg arg funType =
    FunType (funRetType funType) ((funArgs funType) ++ [getArgType arg] )

tabul :: String
tabul = "\n   "

instance Show TypeEnv where
    show env =
            "VENV = " ++ (stringMapToString (varTypeEnv env)) ++
            "\nFENV = " ++ (stringMapToString (funTypeEnv env)) ++
            "\nRENV = " ++ (stringMapToString (recTypeEnv env))

stringMapToString :: (Show a) => Map String a -> String
stringMapToString m = foldrWithKey (\vname -> \vtype -> \s -> s ++
    vname ++ " :: " ++ (show vtype) ++ tabul) tabul m

getSubType :: Type -> Maybe Type
getSubType (TArray t n) = Just t
getSubType (TMap kt vt) = Just vt
getSubType (_) = Nothing

emptyTypeEnv :: TypeEnv
emptyTypeEnv = TypeEnv {
    varTypeEnv = Map.empty,
    funTypeEnv = Map.empty,
    recTypeEnv = Map.empty
}

unique :: (Ord a, Eq a) => [a] -> Bool
unique = uniqueForSorted . List.sort

uniqueForSorted :: Eq a => [a] -> Bool
uniqueForSorted [] = True
uniqueForSorted [_] = True
uniqueForSorted (x : z : xs)
    | x == z = False
    | otherwise = uniqueForSorted (z : xs)

isPrimitiveType :: Type -> Bool
isPrimitiveType vtype = do
    case vtype of
        TBool -> True
        TInt -> True
        TString -> True
        _ -> False

getLTypeFromFType :: FType -> LiftedType
getLTypeFromFType (TFTypeT t) = LTypeT t
getLTypeFromFType (TFTypeF t) = LTypeF t

-- -----------------------------------------------------------------------------
-- ---------------------- EVAL -------------------------------------------------
-- -----------------------------------------------------------------------------

type EvalReader a = ReaderT EvalEnv (StateT Store Result ) a
data Loc = Loc Int deriving (Show, Eq, Ord)
type Store = Map.Map Loc Val

data Val
    = VInt Int
    | VBool Bool
    | VString String
    | VArray Int (Map.Map Int Loc)
    | VMap (Map.Map Val Loc)
    | VRecord (Map.Map RName Loc)
    | VNull
  deriving (Show, Eq, Ord)

data Fun = Fun {
    fargs :: [Arg],
    fbody :: FunBody
} deriving (Show)

type VEnv = Map.Map VName Loc
type FEnv = Map.Map FName (Fun, VEnv)
type REnv = Map.Map RName Rec
type Rec = Map.Map Name Loc

data EvalEnv = EvalEnv {
    vEnv :: VEnv,
    fEnv :: FEnv,
    rEnv :: REnv,
    idList :: [Name]
}

emptyStore :: Store
emptyStore = Map.empty

emptyEvalEnv :: EvalEnv
emptyEvalEnv = EvalEnv {
    vEnv = Map.empty,
    fEnv = Map.empty,
    rEnv = Map.empty,
    idList = []
}

getNextLoc :: EvalReader Loc
getNextLoc = do
    store <- get
    let n = Map.size store
    return $ Loc (Map.size store)

getNextLocs :: Int -> EvalReader [Loc]
getNextLocs n = do
    (Loc i) <- getNextLoc
    return [ (Loc k) | k <- [i..(i+n-1)]]

getLocFromName :: VName -> EvalReader Loc
getLocFromName name = do
    env <- ask
    return $ (vEnv env) Map.! name

instance Show EvalEnv where
    show env =
        "VENV = " ++ (stringMapToString (vEnv env))
        ++ "\nFENV = " ++ (stringMapToString (fEnv env))
        ++ "\nRENV = " ++ (stringMapToString (rEnv env))

storeToString :: Store -> String
storeToString store = "STORE = " ++ (storeMapToString store)

storeMapToString :: Store -> String
storeMapToString store = Map.foldrWithKey (\loc -> \val -> \s -> s ++
    (show loc) ++ " :: " ++ (show val) ++ ", ") "  " store

initVal :: Type -> EvalReader Val
initVal (TArray arrtype n) = return $ VArray (fromInteger n) (Map.empty)

initVal (TMap keytype valtype) = return $ VMap (Map.empty)

initVal (TRecord rid) = do
    let rname = getNameFromId rid
    env <- ask
    let rmap = (rEnv env) Map.! rname
    rmap' <- mapM copyRecField (Map.toList rmap)
    return $ VRecord (Map.fromList rmap')

initVal _ = return $ VNull

copyRecField :: (RName, Loc) -> EvalReader (RName, Loc)
copyRecField (name, loc) = do
    newloc <- getNextLoc
    default_val <- gets (Map.! loc)
    modify (Map.insert newloc default_val)
    return (name, newloc)

printMonadEvalEnv :: String -> EvalReader ()
printMonadEvalEnv msg = do
    env <- ask
    store <- get
    liftIO $ putStrLn ""
    liftIO $ putStrLn $ "-- EVAL Envs: " ++ msg ++ " --"
    liftIO $ print env
    liftIO $ putStrLn $ "-- EVAL Store: " ++ msg ++ ", size = " ++ (show (Map.size store)) ++ " --"
    liftIO $ print (storeToString store)
    liftIO $ putStrLn "-- end EVAL Env/Store"
    return ()

printResultEvalEnv :: EvalEnv -> String -> EvalReader ()
printResultEvalEnv env msg = do
    liftIO $ putStrLn ""
    liftIO $ putStrLn $ "-- EVAL Result Env: " ++ msg ++ " --"
    liftIO $ print env
    liftIO $ putStrLn "-- end EVAL Result Env"
    return ()

printMonadEnv :: String -> CheckReader ()
printMonadEnv msg = do
    env <- ask
    liftIO $ putStrLn ""
    liftIO $ putStrLn $ "-- CHECK Monad Env: " ++ msg ++ " --"
    liftIO $ print env
    liftIO $ putStrLn "-- end CHECK Monad Env"
    return ()

printResultEnv :: TypeEnv -> String -> CheckReader ()
printResultEnv env msg = do
    liftIO $ putStrLn ""
    liftIO $ putStrLn $ "------ CHECK Result Env: " ++ msg ++ " ------"
    liftIO $ print env
    liftIO $ putStrLn "-- end CHECK Result Env"
    return ()

-- -----------------------------------------------------------------------------
-- ---------------------- AUX --------------------------------------------------
-- -----------------------------------------------------------------------------
add :: Val -> Val -> Val
add (VInt m) (VInt n) = VInt (m + n)

sub :: Val -> Val -> Val
sub (VInt m) (VInt n) = VInt (m - n)

mul :: Val -> Val -> Val
mul (VInt m) (VInt n) = VInt (m * n)

diva :: Val -> Val -> EvalReader Val
diva (VInt m) (VInt n) = do
    if (n == 0) then
        lift $ throwError "_ div 0 error"
    else
        return $ VInt (div m n)

lt :: Val -> Val -> Val
lt (VInt m) (VInt n) = VBool (m < n)

lte :: Val -> Val -> Val
lte (VInt m) (VInt n) = VBool (m <= n)

gt :: Val -> Val -> Val
gt (VInt m) (VInt n) = VBool (m > n)

gte :: Val -> Val -> Val
gte (VInt m) (VInt n) = VBool (m >= n)

and' :: Val -> Val -> Val
and' (VBool b) (VBool b') = VBool (b && b')

or' :: Val -> Val -> Val
or' (VBool b) (VBool b') = VBool (b || b')

not' :: Val -> Val
not' (VBool b) = VBool (not b)
