module Home

open System
open Model
open Fable.Helpers.ReactNative
open Elmish

module Cmd =
    open Elmish
    let ofEffect p f =
        Cmd.ofPromise (fun () -> p) () (Result.Ok >> f) (Result.Error >> f)

module Effects =
    open Fable.PowerPack
    let loadItems = 
        promise {
            let rand = Random()
            return
                [0 .. rand.Next(10)]
                |> List.map (fun i -> SwitchItem { title = sprintf "One #%i" i; isChecked = true; id = rand.Next() })
        }

type Msg =
| Reload
| Loaded of Result<Item list, exn>
| Switch of item : SwitchItem
| Click of item : NumberItem *  increase : Boolean
| SeekBar of item : SeekBarItem * value: Single

type Model = { configs : Item list; loading : Boolean }

let update (msg : Msg) model : Model * Cmd<Msg> =
    match msg with
    | Reload -> model, Cmd.ofEffect Effects.loadItems Loaded
    | Loaded (Ok configs) -> { model with configs = configs }, Cmd.none
    | Loaded (Error _) -> model, Cmd.none
    | Switch (item) -> model, Cmd.none
    | Click (item, increase) -> model, Cmd.none
    | SeekBar (item, value) -> model, Cmd.none

let init () = { configs = []; loading = false }, Cmd.ofMsg (Reload)

let rec configToView dispatch item  =
    match item with
    | GroupItem (_, _, _, children) ->
        view [] (children |> List.map (configToView dispatch))
    | SwitchItem x -> 
        switch [] (fun _ -> dispatch <| Switch x) x.isChecked
    | SeekBarItem _ -> 
        view [] [] // slider [ SliderProperties.OnValueChange ( (Func<_,_>) (fun _ -> ())) ]
    | NumberItem _ -> 
        view [] []

let view (model : Model) (dispatch : Msg -> unit) =
    view [ Styles.sceneBackground ]
         [ Styles.button "Reload" (fun () -> dispatch Reload) 
           (model.configs |> List.map (configToView dispatch)) |> view []
         ]
