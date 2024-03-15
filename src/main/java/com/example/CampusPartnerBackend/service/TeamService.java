package com.example.CampusPartnerBackend.service;

import com.example.CampusPartnerBackend.modal.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.CampusPartnerBackend.modal.domain.User;

/**
* @author 13425
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-03-15 11:31:34
*/
public interface TeamService extends IService<Team> {
    Long addTeam(Team team, User loginUser);
}
