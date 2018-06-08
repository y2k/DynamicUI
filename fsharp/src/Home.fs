module Home

open System
open Model
open Elmish
open Fable.Helpers.ReactNative
open Fable.Helpers.ReactNative.Props

module Cmd =
    open Elmish
    let ofEffect p f =
        Cmd.ofPromise (fun () -> p) () (Result.Ok >> f) (Result.Error >> f)

module Effects =
    open Fable.PowerPack
    let loadItems = 
        promise {
            do! Promise.sleep 500
            let rand = Random()
            return
                [0 .. rand.Next(4, 10)]
                |> List.map (fun i -> 
                    match rand.Next(2) % 2 with
                    | 0 -> SwitchItem { title = sprintf "Switch #%i" i; isChecked = true; id = rand.Next() }
                    | _ -> NumberItem { value = rand.Next(); id = rand.Next() })
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
    | Reload -> 
        { model with loading = true; configs = [] }, Cmd.ofEffect Effects.loadItems Loaded
    | Loaded (Ok configs) -> 
        { model with configs = configs; loading = false }, Cmd.none
    | Switch item -> 
        { model with configs = Configs.changeSwitch model.configs item.id }, Cmd.none
    | Click (item, increase) -> 
        { model with configs = Configs.changeNumber model.configs item.id increase }, Cmd.none
    | SeekBar (item, value) -> 
        { model with configs = Configs.changeSeekBar model.configs item.id value }, Cmd.none
    | Loaded (Error _) -> model, Cmd.none

let init () = { configs = []; loading = false }, Cmd.ofMsg (Reload)

let rec configToView dispatch item  =
    match item with
    | GroupItem (_, _, _, children) ->
        children |> List.map (configToView dispatch) |> view []
    | SwitchItem x -> 
        switch [] (fun _ -> dispatch <| Switch x) x.isChecked
    | SeekBarItem _ -> 
        view [] [] // slider [ SliderProperties.OnValueChange ( (Func<_,_>) (fun _ -> ())) ]
    | NumberItem x -> 
        view [ ViewProperties.Style [ FlexStyle.FlexDirection FlexDirection.Row ] ] 
             [ Styles.button "-" (fun () -> dispatch <| Click (x, false))
               text [ TextProperties.Style [ FlexStyle.Flex 1.
                                             TextStyle.TextAlign TextAlignment.Center ] ] 
                    (sprintf "%o" x.value)
               Styles.button "+" (fun () -> dispatch <| Click (x, true))
             ]

let view (model : Model) (dispatch : Msg -> unit) =
    view [ Styles.sceneBackground ]
         [ button [ ButtonProperties.Title (if model.loading then "Loading..." else "Reload")
                    ButtonProperties.OnPress (fun () -> dispatch Reload)
                    ButtonProperties.Disabled model.loading ] [ ]
           (model.configs |> List.map (configToView dispatch)) |> view []
         ]
