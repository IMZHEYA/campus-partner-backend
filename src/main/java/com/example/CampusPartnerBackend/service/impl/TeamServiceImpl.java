package com.example.CampusPartnerBackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.modal.domain.Team;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.modal.domain.UserTeam;
import com.example.CampusPartnerBackend.modal.dto.TeamQuery;
import com.example.CampusPartnerBackend.modal.enums.TeamStatusEnum;
import com.example.CampusPartnerBackend.modal.request.TeamUpdateRequest;
import com.example.CampusPartnerBackend.modal.vo.TeamUserVO;
import com.example.CampusPartnerBackend.modal.vo.UserVO;
import com.example.CampusPartnerBackend.service.TeamService;
import com.example.CampusPartnerBackend.Mapper.TeamMapper;
import com.example.CampusPartnerBackend.service.UserService;
import com.example.CampusPartnerBackend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 13425
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-03-15 11:31:34
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(Team team, User loginUser) {
        //1.请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3.检验信息
        //(1).队伍人数>1且<=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <= 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        //(2).队伍标题 <=20
        String name = team.getName();
        if (name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题过长");
        }
        // 3. 描述<= 512
        String description = team.getDescription();
        if (description.length() > 0 && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //4.status 是否公开，不传默认为0
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        //5.如果status是加密状态，一定要密码 且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        //6.超出时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 过期时间");
        }
        //7.校验用户最多创建5个队伍
        //todo 有bug。可能同时创建100个队伍
        Long userId = loginUser.getId();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = this.count(queryWrapper);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        //8.插入队伍消息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        //9. 插入用户 ==> 队伍关系 到关系表
        UserTeam userTeam = new UserTeam();
        Long teamId = team.getId();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        String searchText = teamQuery.getSearchText();
        if(StringUtils.isNotBlank(searchText)){
            queryWrapper.and(qw -> qw.like("name",searchText).or().like("description",searchText));
        }
        String name = teamQuery.getName();
        if(StringUtils.isNotBlank(name)){
            queryWrapper.like("name",name);
        }
        String description = teamQuery.getDescription();
        if(StringUtils.isNotBlank(description)){
            queryWrapper.like("description",description);
        }
        Integer maxNum = teamQuery.getMaxNum();
        if(maxNum != null && maxNum > 0){
            queryWrapper.eq("maxNum",maxNum);
        }
        Long userId = teamQuery.getUserId();
        if(userId != null && userId > 0) {
            queryWrapper.eq("userId",userId);
        }
        //状态校验
        Integer status = teamQuery.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(teamStatusEnum == null){
            teamStatusEnum = TeamStatusEnum.PUBLIC;
        }

        if(!isAdmin && !TeamStatusEnum.PUBLIC.equals(teamStatusEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        queryWrapper.eq("status",teamStatusEnum.getValue());
        //不展示已过期的队伍
       //要求查询结果中的 "expireTime" 字段的值要么大于当前时间（未过期），要么为空（未设置过期时间）。
        queryWrapper.and(qw -> qw.gt("expireTime",new Date()).or().isNull("expireTime"));
        //查询出来
        List<Team> teamList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }

        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for(Team team : teamList){
            //关联查询创建人的用户信息？
            userId = team.getUserId();
            if(userId == null){
                continue;
            }
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            User user = userService.getUserById(userId);
            //脱敏信息
            if(user != null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if(id == null || id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        boolean isAdmin = userService.isAdmin(loginUser);
        if(oldTeam.getUserId() != loginUser.getId() && !isAdmin){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,team);
        return this.updateById(team);
    }
}




