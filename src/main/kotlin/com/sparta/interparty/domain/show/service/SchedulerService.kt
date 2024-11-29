package com.sparta.interparty.domain.show.service

import org.springframework.scheduling.annotation.Scheduled

class SchedulerService (private val showServiceV2: ShowServiceV2){

    // 1시간마다 Redis 조회수를 MySQL로 동기화
    @Scheduled(cron = "0 0 * * * ?")
    fun hourlyViewCountSync() {
        showServiceV2.syncViewCountsToMySQL()
    }
    // 자정에 랭킹 동기화 및 초기화
    @Scheduled(cron = "0 0 0 * * ?")
    fun nightlyRankingSync() {
        showServiceV2.syncRankingsToMySQL()
    }
}