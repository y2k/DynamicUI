module Home

open System
open Model

open Fable.Helpers.ReactNative
// open Fable.Helpers.ReactNative.Props
open Elmish

// Model
// type Msg =
// | GetDemoData
// | NewDemoData of int
// | BeginWatch
// | Error of exn

type Msg =
| Reload
| Loaded of configs : Item list
| Switch of item : SwitchItem
| Click of item : NumberItem *  increase : Boolean
| SeekBar of item : SeekBarItem * value: Single


type Model = { configs : Item list; loading : Boolean }

// Update
let update (msg : Msg) model : Model * Cmd<Msg> =
    match msg with
    | Loaded (configs) -> failwith "Not Implemented"
    | Switch (item) -> failwith "Not Implemented"
    | Click (item, increase) -> failwith "Not Implemented"
    | SeekBar (item, value) -> failwith "Not Implemented"    
    | Reload -> failwith "FIXME"

// match msg with
// | GetDemoData ->
//     { model with StatusText = "Syncing..." },
//     Cmd.ofPromise Database.createDemoData () NewDemoData Error

// | NewDemoData count ->
//     { model with StatusText = sprintf "Locations: %d" count }, Cmd.none

// | BeginWatch ->
//     model, Cmd.none // Handled one level above

// | Error e ->
//     { model with StatusText = string e.Message }, Cmd.none


let init () = { configs = []; loading = false }, Cmd.ofMsg (Reload)

// View
let view (model : Model) (dispatch : Msg -> unit) =
      view [ Styles.sceneBackground ]
           [ Styles.button "Reload" (fun () -> dispatch Reload)
           ]
