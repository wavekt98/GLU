package com.ssafy.glu.problem.domain.problem.dto.response;

import java.util.Map;

import com.ssafy.glu.problem.domain.common.dto.CommonCodeResponse;
import com.ssafy.glu.problem.domain.problem.domain.Problem;
import com.ssafy.glu.problem.domain.problem.domain.UserProblemStatus;

import lombok.Builder;

@Builder
public record ProblemBaseResponse(
	String problemId,
	String title,
	String content,
	String answer,
	String solution,
	Problem.Status status,

	CommonCodeResponse questionType,
	CommonCodeResponse problemLevel,
	CommonCodeResponse problemType,
	CommonCodeResponse problemTypeDetail,

	Map<String, Object> metadata,

	Boolean isFavorite
) {
	public static ProblemBaseResponse of(Problem problem, Problem.Status status, Boolean isFavorite) {
		if (problem == null)
			return null;
		return ProblemBaseResponse.builder()
			.problemId(problem.getProblemId())
			.title(problem.getTitle())
			.answer(problem.getAnswer())
			.content(problem.getContent())
			.solution(problem.getSolution())
			.status(status)
			.questionType(CommonCodeResponse.of(problem.getQuestionTypeCode()))
			.problemLevel(CommonCodeResponse.of(problem.getProblemLevelCode()))
			.problemType(CommonCodeResponse.of(problem.getProblemTypeCode()))
			.problemTypeDetail(CommonCodeResponse.of(problem.getProblemTypeDetailCode()))
			.metadata(problem.getMetadata())
			.isFavorite(isFavorite)
			.build();
	}

	public static ProblemBaseResponse of(Problem problem, Problem.Status status) {
		return of(problem, status, null);
	}

	public static ProblemBaseResponse of(Problem problem) {
		return of(problem, null);
	}

	public static ProblemBaseResponse of(UserProblemStatus userProblemStatus) {
		return of(userProblemStatus.getProblem(), userProblemStatus.getStatus(), userProblemStatus.getIsFavorite());
	}
}
