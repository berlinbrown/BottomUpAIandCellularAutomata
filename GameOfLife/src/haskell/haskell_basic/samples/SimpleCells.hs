-- -*-haskell-*-
--
--  Author : Berlin Brown
--
--  Created: March 2011
--
--  Copyright (C) 1999-2005 Berlin Brown
--
-- |
-- Maintainer  : berlin.brown at gmail.com
-- Stability   : provisional
-- Portability : portable (depends on GHC), 
-- Tested With: # ghc --version
--
-- The Glorious Glasgow Haskell Compilation System, version 6.12.3
-- ghc --make SimpleCells.hs
-- See: http://code.google.com/p/ainotebook/
--
-- Berlin Brown

import Graphics.UI.Gtk
import Graphics.UI.Gtk.Gdk.GC
import Graphics.UI.Gtk hiding (Color, Point, Object)

defaultFgColor :: Color
defaultFgColor = Color 65535 65535 65535

defaultBgColor :: Color
defaultBgColor = Color 0 0 0

renderScene d ev = do
	dw	   <- widgetGetDrawWindow d
	(w, h) <- widgetGetSize d
	gc     <- gcNew dw
	let fg = Color	(round (65535 * 205))
					(round (65535 * 0))
					(round (65535 * 0))
	gcSetValues gc $ newGCValues { foreground = fg }
	drawPoint dw gc (120, 120)
	drawPoint dw gc (22, 22)
	drawRectangle dw gc True 20 20 20 20
	return True

main :: IO ()	
main = do
	initGUI
	window  <- windowNew
	drawing <- drawingAreaNew
	windowSetTitle window "Cells"
	containerAdd window drawing
	let bg = Color 	(round (65535 * 205))
					(round (65535 * 205))
					(round (65535 * 255))
	widgetModifyBg drawing StateNormal bg
	onExpose drawing (renderScene drawing)
	
	onDestroy window mainQuit
	windowSetDefaultSize window 800 600
	windowSetPosition window WinPosCenter
	widgetShowAll window
	mainGUI
	
-- End of File	