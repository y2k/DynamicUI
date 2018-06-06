module Model

open System

type SwitchItem = SwitchItem of title : String * isChecked : Boolean * id : Int32
type SeekBarItem = SeekBarItem of value : Single * id : Int32
type NumberItem = NumberItem of value : Int32 * id : Int32

type Item =
| GroupItem of title : String * isEnabled : Boolean * id : Int32 * children : Item list
| SwitchItem of SwitchItem
| SeekBarItem of SeekBarItem
| NumberItem of NumberItem

module Configs =
    let changeNumber configs id increase : Item list =
        failwith "TODO"
    let changeSwitch configs id : Item list =
        failwith "TODO"
    let changeSeekBar configs id value : Item list =
        failwith "TODO"
    let flatConfigs configs : Item list =
        failwith "TODO"

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