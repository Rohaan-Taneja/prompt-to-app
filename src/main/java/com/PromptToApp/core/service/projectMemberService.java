package com.PromptToApp.core.service;

import com.PromptToApp.core.Dto.Res.projectMemberResponseDto;

import java.util.List;
import java.util.UUID;

public interface projectMemberService {

    List<projectMemberResponseDto> getProjectMembers(UUID projectId);


    projectMemberResponseDto updateProjectMember(UUID ProjectId);


    Boolean inviteMember(UUID userId , UUID ProjectId);

    Boolean removeProjectMember(UUID projectId , UUID projectMemberId);
}
