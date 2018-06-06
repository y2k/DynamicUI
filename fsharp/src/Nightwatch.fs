module Nightwatch

open Elmish
open Elmish.React
open Elmish.ReactNative
open Elmish.HMR

module App = Home

let setupBackHandler dispatch =    
    let backHandler () =
        true

    Fable.Helpers.ReactNative.setOnHardwareBackPressHandler backHandler


let subscribe (model:App.Model) =
    Cmd.batch [
        Cmd.ofSub setupBackHandler ]


Program.mkProgram App.init App.update App.view
|> Program.withSubscription subscribe
#if RELEASE
#else
|> Program.withConsoleTrace
|> Program.withHMR
#endif
|> Program.withReactNative "nightwatch"
|> Program.run