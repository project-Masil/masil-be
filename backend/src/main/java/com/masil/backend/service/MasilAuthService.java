package com.masil.backend.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.masil.backend.entity.MasilMember;
import com.masil.backend.repository.MasilMemberRepository;

@Service
public class MasilAuthService implements UserDetailsService{
	@Autowired
	private final MasilMemberRepository memberRepository;

	public MasilAuthService(MasilMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	MasilMember member = memberRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return new org.springframework.security.core.userdetails.User(member.getUserEmail(), member.getUserPwd(), new ArrayList<>());
    }
}
