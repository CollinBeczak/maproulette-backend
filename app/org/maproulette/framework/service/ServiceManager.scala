/*
 * Copyright (C) 2020 MapRoulette contributors (see CONTRIBUTORS.md).
 * Licensed under the Apache License, Version 2.0 (see LICENSE).
 */

package org.maproulette.framework.service

import javax.inject.{Inject, Provider, Singleton}
import org.maproulette.data._
import org.maproulette.exception.NotFoundException

/**
  * Class storing references to all the services available.
  *
  * @author mcuthbert
  */
@Singleton
class ServiceManager @Inject() (
    projectService: Provider[ProjectService],
    grantService: Provider[GrantService],
    userService: Provider[UserService],
    followService: Provider[FollowService],
    groupService: Provider[GroupService],
    commentService: Provider[CommentService],
    tagService: Provider[TagService],
    dataService: Provider[DataService],
    challengeService: Provider[ChallengeService],
    challengeListingService: Provider[ChallengeListingService],
    challengeSnapshotService: Provider[ChallengeSnapshotService],
    userMetricService: Provider[UserMetricService],
    virtualProjectService: Provider[VirtualProjectService],
    taskReviewService: Provider[TaskReviewService],
    taskService: Provider[TaskService],
    teamService: Provider[TeamService],
    notificationService: Provider[NotificationService]
) {
  def comment: CommentService = commentService.get()

  def tag: TagService = tagService.get()

  def userMetrics: UserMetricService = userMetricService.get()

  def virtualProject: VirtualProjectService = virtualProjectService.get()

  def getService(itemType: ItemType): ServiceMixin[_] = itemType match {
    case ProjectType()   => this.project
    case UserType()      => this.user
    case GroupType()     => this.group
    case ChallengeType() => this.challenge
    case TagType()       => this.tag
    case GrantType()     => this.grant
    case _               => throw new NotFoundException(s"Service not found for type $itemType")
  }

  def project: ProjectService = projectService.get()

  def grant: GrantService = grantService.get()

  def user: UserService = userService.get()

  def follow: FollowService = followService.get()

  def group: GroupService = groupService.get()

  def data: DataService = dataService.get()

  def challenge: ChallengeService = challengeService.get()

  def challengeListing: ChallengeListingService = challengeListingService.get()

  def challengeSnapshot: ChallengeSnapshotService = challengeSnapshotService.get()

  def taskReview: TaskReviewService = taskReviewService.get()

  def task: TaskService = taskService.get()

  def team: TeamService = teamService.get()

  def notification: NotificationService = notificationService.get()
}
