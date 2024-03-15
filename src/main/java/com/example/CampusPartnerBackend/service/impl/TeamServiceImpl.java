package com.example.CampusPartnerBackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.CampusPartnerBackend.modal.domain.Team;
import com.example.CampusPartnerBackend.service.TeamService;
import com.example.CampusPartnerBackend.Mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 13425
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-03-15 11:31:34
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




