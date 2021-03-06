module View.LoginForm exposing (..)

import Html exposing (..)
import Html.Attributes exposing (type_, placeholder, name)
import Model
import Messages exposing (Msg(..))
import Html.Events exposing (onClick, onInput)
import View.UserInfoView exposing (userInfoView)


view : Model.Model -> Html Msg
view model =
    case model.auth of
        Model.LoggedOut formData ->
            div []
                [ div
                    []
                    [ text ("JavaBin") ]
                , input
                    [ onInput Messages.UpdateUsername
                    , type_ "text"
                    , name "username"
                    , placeholder "Username"
                    ]
                    []
                , input
                    [ onInput Messages.UpdatePassword
                    , type_ "password"
                    , name "password"
                    , placeholder "Password"
                    ]
                    []
                , button [ onClick LogIn ] [ text "Login" ]
                , case formData.loginErr of
                    Nothing ->
                        text ""

                    Just err ->
                        text err
                ]

        Model.LoggedIn token ->
            userInfoView model
