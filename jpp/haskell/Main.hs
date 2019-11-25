-- MIM UW JPP 2018/19 Assignment #1, March 2019
-- author: Michał Szafraniuk, index: 219673

module Main(main) where
import System.Environment
import System.Exit
import Data.Char
import Data.Maybe
import Lib
import Mon

-- some constants
writeln = putStrLn
prolog = "300 400 translate\n"
epilog = "stroke showpage\n"
errorMsg = prolog ++ "/Courier findfont 24 scalefont 0 0 moveto (Error) show\n" ++ epilog
movetoOp = "moveto"
linetoOp = "lineto"
addOp = "add"
mulOp = "mul"
subOp = "sub"
divOp = "div"
closepathOp = "closepath"
translateOp = "translate"
rotateOp = "rotate"

isInteger s = case reads s :: [(Integer, String)] of
  [(_, "")] -> True
  _         -> False

getScaleFactor :: [String] -> Maybe Int
getScaleFactor [] = Just 1
getScaleFactor (n:ns)
    | isInteger n = Just (read n :: Int)
    | otherwise = Nothing

parseArgs :: [String] -> IO Int
parseArgs [] = return 1
parseArgs (n:ns)
    | isInteger n = return (read n :: Int)
    | otherwise = do die "Argument error: scale factor argument must be integer."

-- Stack
data Stack a = Stack [a] deriving Show
emptyStack :: Stack a
emptyStack = Stack []

push :: a -> Stack a -> Stack a
push e (Stack es) = Stack (e:es)

pop :: Stack a -> IO (a, Stack a)
pop (Stack []) = do die errorMsg
pop (Stack (e:es)) = return (e, Stack es)

-- Path
type Path = [Point]

addLine :: [Path] -> Maybe Point -> Point -> IO ([Path], Maybe Point)
addLine paths Nothing p = do die errorMsg
addLine [] (Just q) p = return ([[q, p]], Just p) 
addLine (path:paths) (Just q) p = return ((p:path):paths, Just p)


closePath :: [Path] -> Maybe Point -> IO ([Path], Maybe Point)
closePath paths Nothing = do die errorMsg
closePath [] mp = return ([], mp)
closePath ([p]:paths) mp = return ([p]:paths, mp)
closePath (path:paths) (Just q) = return ((last path:path):paths, Just (last path))

-- analyze
analyze :: [String] -> Transform -> Stack Int -> [Path] -> Maybe Point -> IO [Path]
analyze [] t stack paths point = do return paths
analyze (s:ss) t stack paths point
    | isInteger s = do
                        let x = (read s :: Int)
                        analyze ss t (push x stack) paths point

    | s == addOp = do
                        (x1, stack') <- pop stack
                        (x2, stack'') <- pop stack'
                        analyze ss t (push (x1 + x2) stack'') paths point

    | s == subOp = do
                        (x1, stack') <- pop stack
                        (x2, stack'') <- pop stack'
                        analyze ss t (push (x2-x1) stack'') paths point

    | s == mulOp = do
                        (x1, stack') <- pop stack
                        (x2, stack'') <- pop stack'
                        analyze ss t (push (x1*x2) stack'') paths point

    | s == divOp = do
                        (x1, stack') <- pop stack
                        (x2, stack'') <- pop stack'
                        analyze ss t (push (div x2 x1) stack'') paths point

    | s == movetoOp = do
                        (x2, stack') <- pop stack
                        (x1, stack'') <- pop stack'
                        let
                            p = trpoint t (Point (toRational x1) (toRational x2))
                        analyze ss t stack'' ([p]:paths) (Just p)

    | s == linetoOp = do
                        (x2, stack') <- pop stack
                        (x1, stack'') <- pop stack'
                        let
                            p = trpoint t (Point (toRational x1) (toRational x2))
                        (paths', point') <- addLine paths point p
                        analyze ss t stack'' paths' point'

    | s == closepathOp = do
                        (paths', point') <- closePath paths point
                        analyze ss t stack paths' point'

    | s == translateOp = do
                        (x2, stack') <- pop stack
                        (x1, stack'') <- pop stack'
                        analyze ss (t >< (translate (vec (toRational x1,toRational x2)))) stack'' paths point

    | s == rotateOp = do
                        (x, stack') <- pop stack
                        analyze ss (t >< (rotate (toRational x))) stack' paths point

    | otherwise = do
                        die errorMsg

picture :: [Path] -> Picture
picture [] = []
picture (path:paths)
    | length path < 2 = picture paths
    | otherwise = (path !! 0, path !! 1):(picture ((tail path):paths) )

renderToString :: IntRendering -> String
renderToString [] = []
renderToString (l:ls) =
    show (fst (snd l)) ++ " " ++ show (snd (snd l)) ++ " " ++ movetoOp ++ " " ++
    show (fst (fst l)) ++ " " ++ show (snd (fst l)) ++ " " ++ linetoOp ++ "\n" ++
    (renderToString ls)

renderOutput :: IntRendering -> String
renderOutput ren = prolog ++ (renderToString (reverse ren)) ++ epilog

main = do
    args <- getArgs;
    n <- parseArgs args
    contents <- getContents
    paths <- analyze (words contents) (Transform zeroAngle zeroVec) emptyStack [] Nothing
    putStr (renderOutput (renderScaled n (picture paths)))
