package com.sparta.interparty.domain.show.entity

import com.sparta.interparty.global.entity.TimeStamped
import com.sparta.interparty.domain.show.enums.ShowCategories
import com.sparta.interparty.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "shows")
class Show() : TimeStamped() {

    // Id 를 제외한 모든 필드에 대한 생성자
    constructor(
        name: String,
        contents: String,
        address: String,
        price: Long,
        totalSeats: Long,
        startDateTime: LocalDateTime,
        category: ShowCategories,
        manager: User? = null
    ) : this() {
        this.name = name
        this.contents = contents
        this.address = address
        this.price = price
        this.totalSeats = totalSeats
        this.startDateTime = startDateTime
        this.category = category
        this.manager = manager
    }

    // 자동 생성 시를 고려해 null 을 허용
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @Column(nullable = false) var name: String = ""
    @Column(nullable = false) var contents: String = ""
    @Column(nullable = false) var address: String = ""
    @Column(nullable = false) var price: Long = 0
    @Column(nullable = false) var totalSeats: Long = 0

    // 반드시 필요한 non-nullable 컬럼이지만, 공연 일시에 대한 기본값을 생성할 수 없으므로 null 을 허용
    @Column(nullable = false) var startDateTime: LocalDateTime? = null

    // 검색 편의성을 위해 카테고리를 필수 지정
    @Column(nullable = false) @Enumerated(EnumType.STRING)
    var category: ShowCategories = ShowCategories.ETC

    // 공연을 등록한 사용자(매니저)를 참조하기 위한 외래키
    @JoinColumn(name = "manager_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    var manager: User? = null

    // SoftDelete 를 위한 컬럼
    @Column(nullable = false) var isDeleted: Boolean = false

    // 캐싱을 위한 필드 추가
    @Column
    var viewCount: Int = 0

    @ElementCollection
    @CollectionTable(name = "show_rankings", joinColumns = [JoinColumn(name = "show_id")])
//    @MapKeyColumn(name = "rank")
    @Column
    var rankings: MutableMap<Int, UUID> = mutableMapOf()
}