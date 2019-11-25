module SkelBlah where

-- Haskell module generated by the BNF converter

import AbsBlah
import ErrM
type Result = Err String

failure :: Show a => a -> Result
failure x = Bad $ "Undefined case: " ++ show x

transId :: Id -> Result
transId x = case x of
  Id string -> failure x
transProgram :: Program -> Result
transProgram x = case x of
  Prog defs -> failure x
transDef :: Def -> Result
transDef x = case x of
  DVar vardecl -> failure x
  DFun fundef -> failure x
transFunDef :: FunDef -> Result
transFunDef x = case x of
  DFunAll ftype id args funbody -> failure x
transFunBody :: FunBody -> Result
transFunBody x = case x of
  DFunBody stmts retstmt -> failure x
transStmt :: Stmt -> Result
transStmt x = case x of
  StmtVarDec vardecl -> failure x
  StmtFunDef fundef -> failure x
  SExpr expr -> failure x
  SEmpty -> failure x
  SBlock stmts -> failure x
  SWhile expr stmt -> failure x
  StmtIfelse expr stmt1 stmt2 -> failure x
  StmtIf expr stmt -> failure x
  StmtPrint expr -> failure x
transRetStmt :: RetStmt -> Result
transRetStmt x = case x of
  StmtRetExpr expr -> failure x
transArg :: Arg -> Result
transArg x = case x of
  ArgVal type_ id -> failure x
  ArgFun ftype id ftypes -> failure x
  ArgVar type_ id -> failure x
  ArgInout type_ id -> failure x
transVarDecl :: VarDecl -> Result
transVarDecl x = case x of
  DVarSimple type_ id -> failure x
  DVarList type_ id ids -> failure x
  DVarExpr type_ id expr -> failure x
  DVarRecord vardecls id -> failure x
transAcc :: Acc -> Result
transAcc x = case x of
  AccEmpty -> failure x
  AccArray expr acc -> failure x
  AccMap expr acc -> failure x
  AccApp exprs acc -> failure x
  AccRec id acc -> failure x
transExpr :: Expr -> Result
transExpr x = case x of
  EString string -> failure x
  EInt integer -> failure x
  ETrue -> failure x
  EFalse -> failure x
  EAcc id acc -> failure x
  EPIncr expr -> failure x
  EPDecr expr -> failure x
  EIncr expr -> failure x
  EDecr expr -> failure x
  ETimes expr1 expr2 -> failure x
  EDiv expr1 expr2 -> failure x
  EPlus expr1 expr2 -> failure x
  EMinus expr1 expr2 -> failure x
  ELt expr1 expr2 -> failure x
  EGt expr1 expr2 -> failure x
  ELtEq expr1 expr2 -> failure x
  EGtEq expr1 expr2 -> failure x
  EEq expr1 expr2 -> failure x
  ENEq expr1 expr2 -> failure x
  EAnd expr1 expr2 -> failure x
  EOr expr1 expr2 -> failure x
  ENot expr -> failure x
  EAss expr1 ass expr2 -> failure x
transAss :: Ass -> Result
transAss x = case x of
  AssPure -> failure x
  AssPlus -> failure x
  AssMinus -> failure x
  AssTimes -> failure x
  AssDiv -> failure x
transType :: Type -> Result
transType x = case x of
  TBool -> failure x
  TInt -> failure x
  TString -> failure x
  TArray type_ integer -> failure x
  TRecord id -> failure x
  TMap type_1 type_2 -> failure x
transFType :: FType -> Result
transFType x = case x of
  TFTypeT type_ -> failure x
  TFTypeF ftype -> failure x
