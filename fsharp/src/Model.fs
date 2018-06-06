module Model

open System

type SwitchItem = { title : String; isChecked : Boolean; id : Int32 }
type SeekBarItem = SeekBarItem of value : Single * id : Int32
type NumberItem = NumberItem of value : Int32 * id : Int32

type Item =
| GroupItem of title : String * isEnabled : Boolean * id : Int32 * children : Item list
| SwitchItem of SwitchItem
| SeekBarItem of SeekBarItem
| NumberItem of NumberItem

module Configs =
    let changeNumber configs id increase : Item list = configs
    let changeSwitch configs id : Item list = configs
    let changeSeekBar configs id value : Item list = configs
    let flatConfigs configs : Item list = configs

type LocationId = string

[<RequireQualifiedAccess>]
type LocationStatus =
| Ok
| Alarm of string

type LocationCheckRequest = {
    LocationId : LocationId
    Name: string
    Address: string
    Status : LocationStatus option
    Date : DateTime option
}