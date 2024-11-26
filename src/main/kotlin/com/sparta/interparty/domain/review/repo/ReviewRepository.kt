package com.sparta.interparty.domain.review.repo

import com.sparta.interparty.domain.review.entity.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {

    // 특정 사용자가 특정 쇼에 작성한 리뷰를 조회
    fun findByUserIdAndShowId(userId: Long, showId: Long): Review?

    // 특정 쇼에 포함된 모든 리뷰를 조회
    fun findAllByShowId(showId: Long): List<Review>
}
