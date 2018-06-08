module Model

open System

type SwitchItem = { title : String; isChecked : Boolean; id : Int32 }
type SeekBarItem = { value : Single; id : Int32 }
type NumberItem = { value : Int32; id : Int32 }

type Item =
| GroupItem of title : String * isEnabled : Boolean * id : Int32 * children : Item list
| SwitchItem of SwitchItem
| SeekBarItem of SeekBarItem
| NumberItem of NumberItem

module Configs =
    let changeNumber configs id increase : Item list = configs
    let changeSwitch configs id : Item list = 
        configs
        |> List.map (fun x -> function
                              | SwitchItem x -> SwitchItem { x with isChecked = not x.isChecked }
                              | x -> x)
    let changeSeekBar configs id value : Item list = configs
    let flatConfigs configs : Item list = configs