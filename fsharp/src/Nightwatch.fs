module Nightwatch

open Elmish
open Elmish.React
open Elmish.ReactNative
open Elmish.HMR

Program.mkProgram Home.init Home.update Home.view
#if RELEASE
#else
|> Program.withConsoleTrace
|> Program.withHMR
#endif
|> Program.withReactNative "nightwatch"
|> Program.run