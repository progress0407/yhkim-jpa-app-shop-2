package jpa.app.shop.service;

import java.util.List;
import jpa.app.shop.domain.Member;
import jpa.app.shop.repository.MemberRepository;
import jpa.app.shop.validator.MemberValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberService {

	private final MemberRepository memberRepository;

	private final MemberValidator memberValidator;

	// 회원 가입
	@Transactional
	public Long join(Member member) {

		memberValidator.validateDuplicateMember(member);

		return memberRepository.save(member);
	}

	// 회원 전체 조회
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}

	// 회원 조회
	public Member findOne(Long memberId) {
		return memberRepository.findOne(memberId);
	}

	// 회원 수정
	@Transactional
	public void update(Long id, String name) {
		Member findMember = memberRepository.findOne(id);
		findMember.setName(name); // 변경감지
	}

	// 회원 모두 제거
	@Transactional
	public void removeAll() {
		memberRepository.clear();
	}
}
