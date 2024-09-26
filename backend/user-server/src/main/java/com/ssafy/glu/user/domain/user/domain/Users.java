package com.ssafy.glu.user.domain.user.domain;

import java.time.LocalDate;

import org.springframework.util.StringUtils;

import com.ssafy.glu.user.global.shared.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	private String loginId;
	private String nickname;
	private String password;
	private LocalDate birth;
	@Builder.Default
	private Boolean isDeleted = false;
	@Builder.Default
	private Integer stage = 0;
	@Builder.Default
	private Integer exp = 0;
	@Builder.Default
	private Integer dayCount = 0;

	public void updateUser(String password, String nickname, LocalDate birth) {
		if(StringUtils.hasText(password)) this.password = password;
		if(StringUtils.hasText(nickname)) this.nickname = nickname;
		if (birth != null) this.birth = birth;
	}

	public void deleteUser() {
		this.isDeleted = true;
	}

	public Integer updateStage(Integer score) {
		this.exp += score;

		if (stage <= 5 && exp >= 100) {
			stage += 1;
			exp -= 100;
		}

		if (exp < 0) exp = 0;

		return this.stage;
	}

	public Integer getStage() {
		return stage + 1;
	}

	public void updateDayCount() {
		this.dayCount++;
	}

	public void resetDayCount() {
		this.dayCount = 0;
	}

}
