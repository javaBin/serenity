package controllers.helpers

import play.api.routing.Router.Routes

trait RouterCtrl {

  def withRoutes(): Routes

}