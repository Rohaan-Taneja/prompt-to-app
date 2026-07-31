package com.PromptToApp.core.service.serviceImpl;

import com.PromptToApp.core.CustomExceptionHandling.ResourceNotFoundException;
import com.PromptToApp.core.Dto.Res.projectMemberResponseDto;
import com.PromptToApp.core.Entity.ProjectMember;
import com.PromptToApp.core.repository.projectMemberRepository;
import com.PromptToApp.core.service.projectMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class projectMemberServiceImpl implements projectMemberService {


    private final projectMemberRepository projectMember_repo;


      
    public List<projectMemberResponseDto> getProjectMembers(UUID projectId) {
        return List.of();
    }


    public ProjectMember getProjectMember(UUID projectId, UUID memberId) {

        log.info("i am in get project member");

        Optional<ProjectMember> member= projectMember_repo.findByProjectIdAndMemberId(projectId , memberId);

        if(member.isPresent()){
            log.info("member is present");
        }
        else{
            log.info("member is not present {}" , member);

        }

        return member.get();

    }


    public projectMemberResponseDto updateProjectMember(UUID ProjectId) {
        return null;
    }

      
    public Boolean inviteMember(UUID userId, UUID ProjectId) {
        return null;
    }

      
    public Boolean removeProjectMember(UUID projectId, UUID projectMemberId) {
        return null;
    }
}
