package com.ssafy.glu.problem.domain.problem.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ssafy.glu.problem.domain.problem.domain.Problem;
import com.ssafy.glu.problem.domain.problem.domain.ProblemMemo;
import com.ssafy.glu.problem.domain.problem.domain.UserProblemFavorite;
import com.ssafy.glu.problem.domain.problem.domain.UserProblemLog;
import com.ssafy.glu.problem.domain.problem.domain.UserProblemStatus;
import com.ssafy.glu.problem.domain.problem.dto.request.ProblemMemoCreateRequest;
import com.ssafy.glu.problem.domain.problem.dto.request.ProblemSearchCondition;
import com.ssafy.glu.problem.domain.problem.dto.response.ProblemBaseResponse;
import com.ssafy.glu.problem.domain.problem.dto.response.ProblemMemoResponse;
import com.ssafy.glu.problem.domain.problem.exception.ProblemNotFoundException;
import com.ssafy.glu.problem.domain.problem.exception.UserProblemStatusNotFoundException;
import com.ssafy.glu.problem.domain.problem.repository.ProblemRepository;
import com.ssafy.glu.problem.domain.problem.repository.UserProblemFavoriteRepository;
import com.ssafy.glu.problem.domain.problem.repository.UserProblemLogRepository;
import com.ssafy.glu.problem.domain.problem.repository.UserProblemStatusRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
	private final ProblemRepository problemRepository;
	private final UserProblemFavoriteRepository userProblemFavoriteRepository;
	private final UserProblemLogRepository userProblemLogRepository;
	private final UserProblemStatusRepository userProblemStatusRepository;

	@Override
	public Page<ProblemBaseResponse> getProblemListByLog(Long userId, ProblemSearchCondition condition,
		Pageable pageable) {
		return userProblemLogRepository.findAllProblemInLogByCondition(userId, condition, pageable)
			.map(problem -> ProblemBaseResponse.of(problem, condition.status()));
	}

	@Override
	public ProblemMemoResponse createProblemMemo(Long userId, String problemId, ProblemMemoCreateRequest request) {
		// userId와 problemId로 UserProblemStatus를 찾기
		UserProblemStatus userProblemStatus = userProblemStatusRepository.findByUserIdAndProblem_ProblemId(userId,
				problemId)
			.orElseThrow(UserProblemStatusNotFoundException::new);

		// 해당 userId와 problemId에서 가장 큰 memoIndex 값을 찾고, 없으면 1로 설정
		Long memoIndex = generateMemoIndex(userProblemStatus);

		// 찾은 UserProblemStatus의 메모 목록에 새로운 메모 추가
		userProblemStatus.getMemoList().add(
			ProblemMemo.builder()
				.memoIndex(memoIndex)
				.content(request.content())
				.build()
		);

		// 변경된 상태 저장
		userProblemStatusRepository.save(userProblemStatus);

		// 메모 저장 후 응답 생성
		return ProblemMemoResponse.of(memoIndex, request.content());
	}

	@Override
	public Page<ProblemMemoResponse> getProblemMemoList(Long userId, String problemId, Pageable pageable) {
		Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
		return null;
	}

	@Override
	public Page<ProblemBaseResponse> getUserProblemFavoriteList(Long userId, ProblemSearchCondition condition,
		Pageable pageable) {
		return userProblemFavoriteRepository.findAllFavoriteProblem(userId, condition, pageable)
			.map(problem -> {
				// 마지막 UserProblemLog를 problemId로 조회
				Optional<UserProblemLog> lastLog = userProblemLogRepository.findFirstByUserIdAndProblem(userId,
					problem);

				log.info("Problem ID: {}", problem.getProblemId());
				log.info("Last Log Found: {}", lastLog.isPresent());

				Problem.Status status = lastLog.map(
					log -> log.isCorrect() ? Problem.Status.CORRECT : Problem.Status.WRONG).orElse(null);

				// ProblemBaseResponse에 status 추가
				return ProblemBaseResponse.of(problem, status);
			});
	}

	@Override
	public void createUserProblemFavorite(Long userId, String problemId) {
		log.info("===== 문제 찜 요청 - 유저 : {}, 문제: {} =====", userId, problemId);

		// 문제 존재 여부 확인
		Problem problem = getProblemOrThrow(problemId);
		log.info("===== 문제 [{}] 찾았습니다 =====", problem);

		UserProblemFavorite userProblemFavorite = UserProblemFavorite.builder()
			.userId(userId)
			.problem(problem)
			.build();
		log.info("===== 문제 찜 추가 - 유저 : {}, 문제: {} =====", userId, problemId);

		userProblemFavoriteRepository.save(userProblemFavorite);
	}

	@Override
	public void deleteUserProblemFavorite(Long userId, String problemId) {
		log.info("===== 문제 찜 취소 - 유저 : {}, 문제 : {} =====", userId, problemId);

		// 문제 존재 여부 확인
		Problem problem = getProblemOrThrow(problemId);
		log.info("===== 문제 [{}] 찾았습니다 =====", problem);

		userProblemFavoriteRepository.deleteByUserIdAndProblem(userId, problem);
	}

	// 문제 존재 여부 판단
	private Problem getProblemOrThrow(String problemId) {
		return problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
	}

	// userProblemStatus에 있는 memolist 중에서 가장 큰 index 값보다 + 1 반환
	// 중복되지 않는 index 값을 할당하기 위함
	private Long generateMemoIndex(UserProblemStatus userProblemStatus) {
		// 해당 userId와 problemId에서 memoList가 비어 있으면 1을 반환하고, 그렇지 않으면 가장 큰 인덱스에 1을 더함
		return userProblemStatus.getMemoList().isEmpty() ? 1L :
			userProblemStatus.getMemoList().stream()
				.map(ProblemMemo::getMemoIndex)
				.max(Long::compareTo)
				.orElse(0L) + 1L;
	}
}