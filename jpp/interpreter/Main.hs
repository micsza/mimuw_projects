module Main where

    import System.Environment
    import System.Exit
    import System.IO
    import Debug.Trace

    import ErrM
    import ParBlah
    import AbsBlah
    import Check
    import Eval

    exitPrintLog :: String -> IO ()
    exitPrintLog err = do
        putStrLn err
        exitFailure

    runParser = pProgram . myLexer

    compile :: String -> IO ()
    compile s = case runParser s of
        Bad err -> exitPrintLog $ "Syntax error. " ++ err
        Ok tree -> do
            check <- runTypeCheck tree
            case check of
                Left err -> exitPrintLog $ "Type error. " ++ err
                Right _ -> do
                    res <- runEval tree
                    case res of
                        Left err -> exitPrintLog $ "Eval error: " ++ err
                        Right _ -> do
                            return ()

    runFile :: String -> IO ()
    runFile file = readFile file >>= compile

    main :: IO ()
    main = getArgs >>= mapM_ runFile
