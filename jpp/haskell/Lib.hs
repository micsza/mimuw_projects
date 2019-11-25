-- MIM UW JPP 2018/19 Assignment #1, March 2019
-- author: Michał Szafraniuk, index: 219673

module Lib where
    import Mon
    import Data.Ratio

    -- R, R2, aux functions
    type R = Rational
    type R2 = (R, R)

    bhaskaraApprox :: R -> R
    bhaskaraApprox x = (4 * x * (180 - x)) / (40500 - x * (180 -x))

    sinus :: R -> R
    sinus x
        | (rmod x 360) <= 180 = bhaskaraApprox (rmod x 360)
        | otherwise = (-1) * bhaskaraApprox ((rmod x 360) - 180)

    cosinus :: R -> R
    cosinus x = sinus (90 - x)

    rmod :: R -> Int -> R
    rmod r m = (mod (numerator r) (denominator r * toInteger m)) % denominator r


    -- Point
    data Point = Point R R

    xCoordinate :: Point -> R
    xCoordinate (Point x y) = x

    yCoordinate :: Point -> R
    yCoordinate (Point x y) = y

    instance Show Point where
        show (Point x1 x2) = "Point<" ++ show x1 ++ ", " ++ show x2 ++ ">"

    instance Eq Point where
        Point x1 x2  == Point y1 y2 = x1 == y1 && x2 == y2

    point :: R2 -> Point
    point (a, b) = Point a b

    zeroPoint :: Point
    zeroPoint = Point 0 0


    -- Vector
    data Vec = Vec Point

    instance Show Vec where
        show (Vec (Point v1 v2)) = "Vector<" ++ show v1 ++ ", " ++ show v2 ++ ">"

    instance Eq Vec where
        Vec p == Vec q = p == q

    instance Mon Vec where
        m1 = Vec (Point 0 0)
        Vec (Point v1 v2) >< Vec (Point u1 u2) = Vec (Point (v1 + u1) (v2 + u2))

    vec :: R2 -> Vec
    vec u = Vec (point u)

    zeroVec :: Vec
    zeroVec = Vec zeroPoint


    -- Section
    type Section = (Point, Point)

    section :: R -> R -> R -> R -> Section
    section a b c d = ( point (a, b), point (c, d))

    sectionFromPoints :: Point -> Point -> Section
    sectionFromPoints p q = (p, q)


    -- Picture
    type Picture = [Section]

    line :: (R,R) -> (R,R) -> Picture
    line p q = [(point p, point q)]

    rectangle :: R -> R -> Picture
    rectangle w h = [(point (0, 0), point (w, 0)), (point (w,0), point (w,h)), (point (w,h), point (0,h)), (point (0,h), point (0,0))]

    infixr 5 &
    (&) :: Picture -> Picture -> Picture
    pic1 & pic2 = pic1 ++ pic2


    -- IntLine
    type IntLine = ((Int,Int), (Int, Int))


    -- IntRendering
    type IntRendering = [IntLine]

    renderScaled :: Int -> Picture -> IntRendering
    renderScaled n pic = [ ((scaleAndRound n a, scaleAndRound n b), (scaleAndRound n c, scaleAndRound n d )) | (Point a b, Point c d) <- pic]

    scaleAndRound :: Int -> R -> Int
    scaleAndRound n r = round ((toInteger n % 1) * r)


    -- Angle
    type Angle = R
    zeroAngle :: Angle
    zeroAngle = 0


    -- Transform
    data Transform = Transform Angle Vec

    instance Eq Transform where
        Transform o1 v1 == Transform o2 v2 = o1 == o2 && v1 == v2

    instance Show Transform where
        show (Transform r vec) = "Transform<angle = " ++ show r ++ ", vector = " ++ show vec ++ ">"

    translate :: Vec -> Transform
    translate vec = Transform 0 vec

    rotate :: R -> Transform
    rotate r = Transform r zeroVec

    fullCircle :: R
    fullCircle = 360

    instance Mon Transform where
        m1 = Transform zeroAngle zeroVec
        Transform o1 vec1 >< Transform o2 vec2 = Transform (o1+o2) (vec1 >< vec2)

    trpoint :: Transform -> Point -> Point
    trpoint (Transform o (Vec (Point v1 v2))) (Point x1 x2) =
        Point (x1 * cosinus o - x2 * sinus o + v1) (x1 * sinus o + x2 * cosinus o + v2)

    trvec :: Transform -> Vec -> Vec
    trvec t (Vec p) = Vec (trpoint t p)

    trsection :: Transform -> Section -> Section
    trsection t (p, q) = (trpoint t p, trpoint t q)

    transform :: Transform -> Picture -> Picture
    transform t pic = [trsection t sec | sec <- pic]
