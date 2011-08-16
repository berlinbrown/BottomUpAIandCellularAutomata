{-# LINE 1 "./Graphics/UI/Gtk/Misc/DrawingArea.chs" #-}
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
-- Portability : portable (depends on GHC)
--
-- Berlin Brown
-- Main hs

import Graphics.UI.Gtk
main = do
	initGUI
	window <- windowNew
	window `onDestroy` mainQuit
	windowSetDefaultSize window 800 600
	windowSetPosition window WinPosCenter
	widgetShowAll window
	mainGUI
	
-- End of File	