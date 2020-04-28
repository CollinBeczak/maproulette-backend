/*
 * Copyright (C) 2020 MapRoulette contributors (see CONTRIBUTORS.md).
 * Licensed under the Apache License, Version 2.0 (see LICENSE).
 */

package org.maproulette.framework.service

import org.maproulette.framework.model.User
import org.maproulette.framework.util.{FrameworkHelper, UserMetricsTag}
import org.maproulette.models.Task
import play.api.Application

/**
  * @author mcuthbert
  */
class UserMetricsServiceSpec(implicit val application: Application) extends FrameworkHelper {
  val service: UserMetricService = this.serviceManager.userMetrics
  val userService: UserService   = this.serviceManager.user

  "UserMetricService" should {
    "get metrics for a user" taggedAs UserMetricsTag in {
      //TODO expand these tests.
      val insertedUser =
        this.userService.create(this.getTestUser(19, "UpdateUserService"), User.superUser)
      val userMetrics =
        this.service.getMetricsForUser(
          insertedUser.id,
          insertedUser,
          -1,
          -1,
          -1,
          "",
          "",
          "",
          "",
          "",
          ""
        )
    }

    "updates the users score" taggedAs UserMetricsTag in {
      val insertedUser =
        this.userService.create(this.getTestUser(19, "UpdateUserService"), User.superUser)
      val updatedUser = this.service.updateUserScore(
        Some(Task.STATUS_FIXED),
        Some(1000),
        Some(1),
        true,
        true,
        Some(0),
        insertedUser.id
      )
    }
  }
  override implicit val projectTestName: String = "UserMetricsSpecProject"
}
