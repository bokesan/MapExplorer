-- rotate map beu 180°

-- edit width and height first


module Main (main) where

import Char

main = interact doit

width, height :: Int
width = 34
height = 22


doit :: String -> String
doit inp = unlines (map (\line -> unwords (map rot180 (words line))) (lines inp))



rot180 :: String -> String
rot180 s | isLocation s = rotLoc s
	 | isRange s = rotLoc (rangeStart s) ++ "-" ++ rotLoc (rangeEnd s)
	 | otherwise = s


isLocation :: String -> Bool
isLocation s = length s >= 2 && Char.isUpper (head s) && all Char.isDigit (tail s)

isRange :: String -> Bool
isRange s = case break ('-'==) s of
	      (a, ('-':b)) -> isLocation a && isLocation b
	      _ -> False

rangeStart, rangeEnd :: String -> String
rangeStart s = case break ('-'==) s of
	        (a, _) -> a

rangeEnd s = case break ('-'==) s of
	      (a, ('-':b)) -> b



rotLoc :: String -> String
rotLoc (r:c) = let row = 1 + ord r - ord 'A'
		   col = (read c) :: Int
	       in
	           chr (height + ord 'A' - row) : show (width + 1 - col)
