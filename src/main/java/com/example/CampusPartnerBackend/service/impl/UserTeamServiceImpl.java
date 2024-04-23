package com.example.CampusPartnerBackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.CampusPartnerBackend.modal.domain.UserTeam;
import com.example.CampusPartnerBackend.service.UserTeamService;
import com.example.CampusPartnerBackend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 13425
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-03-15 11:33:44
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




