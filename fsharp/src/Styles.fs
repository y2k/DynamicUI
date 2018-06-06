module internal Styles

open Fable.Helpers.ReactNative
open Fable.Helpers.ReactNative.Props

let [<Literal>] brandPrimary = "#428bca"
let [<Literal>] brandInfo = "#5bc0de"
let [<Literal>] brandSuccess = "#5cb85c"
let [<Literal>] brandDanger = "#d9534f"
let [<Literal>] brandWarning = "#f0ad4e"
let [<Literal>] brandSidebar = "#252932"

let [<Literal>] inverseTextColor = "#000"

let [<Literal>] textColor = "#FFFFFF"

let [<Literal>] shadowColor = "#000000"

let [<Literal>] backgroundColor = "#615A5B"
let [<Literal>] inputBackgroundColor = "#251D1C"

let [<Literal>] touched = "#5499C4"

let [<Literal>] fontSizeBase = 15.
let [<Literal>] smallFontSize = 10.
let [<Literal>] mediumFontSize = 12.
let [<Literal>] titleFontSize = 17.

let [<Literal>] borderRadius = 4.


let inline renderText fontSize =
    TextProperties.Style [ 
        TextStyle.Color textColor
        TextStyle.TextAlign TextAlignment.Center
        FlexStyle.Margin 3.
        TextStyle.FontSize fontSize
      ]

let inline defaultText<'a> = renderText fontSizeBase
let inline mediumText<'a> = renderText mediumFontSize
let inline smallText<'a> = renderText smallFontSize
let inline titleText<'a> = renderText titleFontSize

let inline whitespace<'a> = text [ smallText ] ""

let inline sceneBackground<'a> =
    ViewProperties.Style [ 
        FlexStyle.AlignSelf Alignment.Stretch
        FlexStyle.Padding 20.
        ViewStyle.ShadowColor shadowColor
        ViewStyle.ShadowOpacity 0.8
        ViewStyle.ShadowRadius 3.
        FlexStyle.JustifyContent JustifyContent.FlexStart
        FlexStyle.Flex 1.
        ViewStyle.BackgroundColor backgroundColor
      ]

let inline viewPagerBackground<'a> =
    ViewPagerAndroidProperties.Style [
        FlexStyle.AlignSelf Alignment.Stretch
        FlexStyle.Padding 20.
        ViewStyle.ShadowColor shadowColor
        ViewStyle.ShadowOpacity 0.8
        ViewStyle.ShadowRadius 3.
        FlexStyle.JustifyContent JustifyContent.Center
        FlexStyle.Flex 1.
        ViewStyle.BackgroundColor backgroundColor
      ]
      
let inline button label onPress =
    button [
        ButtonProperties.Title label
        ButtonProperties.OnPress onPress
    ] [ ]
