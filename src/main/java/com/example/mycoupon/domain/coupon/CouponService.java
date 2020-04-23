package com.example.mycoupon.domain.coupon;

import com.example.mycoupon.domain.coupon.Coupon;
import com.example.mycoupon.domain.couponInfo.CouponInfo;
import com.example.mycoupon.exceptions.CouponNotFoundException;
import com.example.mycoupon.domain.couponInfo.CouponInfoRepository;
import com.example.mycoupon.domain.coupon.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    private final CouponInfoRepository couponInfoRepository;

    @Autowired
    public CouponService(CouponRepository couponRepository, CouponInfoRepository couponInfoRepository) {
        this.couponRepository = couponRepository;
        this.couponInfoRepository = couponInfoRepository;
    }

    public String getUUIDCouponCode() {
        // format : uuid
        return UUID.randomUUID().toString();
    }

    public Date getRandomExpiredAt() {
        // TODO: add random expired hours from now date
        return new Date();
    }

    // TODO: 트랜잭션 격리 레벨 설정
    @Transactional
    public void save(int n, long memberId) {
        // TODO: should validate n is over MAX_LIMIT?
        // 1. create Coupon instance -> save
        // 2. create CouponInfo instance -> set coupon_id to 1 instance's id.
        // 3. save CouponInfo instance
        // 4. 1~3번을 트랜잭션으로 묶기. (트랜잭션 격리레벨 설정)
        // 5. 1~4번을 n번 반복하기.
        // TODO: 5번의 경우 서비스 레벨에서 반복하면 안됨. 트랜잭션 묶이는 단위가 한꺼번에 되버림! 멀티쓰레드 코딩 가능한지 찾아보기.
        Coupon coupon = Coupon.builder()
                .memberId(memberId)
                .code(getUUIDCouponCode())
                .expiredAt(getRandomExpiredAt())
                .build();

        Coupon couponResult = couponRepository.save(coupon);
        CouponInfo couponInfo = CouponInfo.builder().coupon(couponResult).isEnabled(true).build();
        // TODO: setting coupon_id or coupon.
        couponInfoRepository.save(couponInfo);

    }

    @Transactional
    public String assignToUser(long id) {
        Coupon coupon = couponRepository.findByFreeUser();
        coupon.setMemberId(id);
        // TODO: 트랜잭션 레벨 고려 (쿠폰을 멤버에 할당하는 도중, 다른 트랜잭션에서 이 쿠폰에 접근하거나 유저를 할당하면 안됨.)
        // TODO: 이것만으로 DB에 update 반영이 되는지? 반영이 안되면 update 쿼리 또 필요. -> test 해볼 것.
        // 영속성 컨텍스트에서 플러시 시점에 스냅샷과 엔티티를 비교해서 변경여부를 체크.
        // 쓰기 지연 저장소에 저장된 수정 쿼리를 DB에 전달 (update query 반영) ->
        return coupon.getCode();
    }

    @Transactional
    public void updateIsEnabledCouponById(String code, boolean isEnabled) throws CouponNotFoundException {
        // TODO : transaction 레벨 설정
        //couponInfoRepository.updateByCode(code, isEnabled);
        Coupon coupon = couponRepository.findByCode(code);
        if(coupon == null) {
            throw new CouponNotFoundException(code);
        }
        coupon.getCouponInfo().setEnabled(isEnabled);
    }

    public Optional<List<Coupon>> findExpiredToday() {
        return couponRepository.findByExpiredToday();
    }
}