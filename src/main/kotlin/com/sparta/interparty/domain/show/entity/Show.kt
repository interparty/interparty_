package com.sparta.interparty.domain.show.entity

import com.sparta.interparty.global.entity.TimeStamped
import com.sparta.interparty.domain.show.enums.ShowCategories
import com.sparta.interparty.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "shows")
class Show(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null, // 자동 생성 ID

    @Column(nullable = false)
    var name: String = "", // 공연 이름

    @Column(nullable = false)
    var contents: String = "", // 공연 내용

    @Column(nullable = false)
    var address: String = "", // 공연 주소

    @Column(nullable = false)
    var price: Long = 0, // 공연 가격

    @Column(nullable = false)
    var totalSeats: Long = 0, // 총 좌석 수

    @Column(nullable = false)
    var startDateTime: LocalDateTime? = null, // 공연 시작 일시

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: ShowCategories = ShowCategories.ETC, // 공연 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = true)
    var manager: User? = null, // 공연 매니저

    @Column(nullable = false)
    var isDeleted: Boolean = false // Soft Delete 여부
) : TimeStamped()
