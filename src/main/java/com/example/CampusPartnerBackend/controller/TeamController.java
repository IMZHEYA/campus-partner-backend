package com.example.CampusPartnerBackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.CampusPartnerBackend.common.BaseResponse;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.common.ResultUtils;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.modal.domain.Team;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.modal.domain.UserTeam;
import com.example.CampusPartnerBackend.modal.dto.TeamQuery;
import com.example.CampusPartnerBackend.modal.request.*;
import com.example.CampusPartnerBackend.modal.request.team.TeamAddRequest;
import com.example.CampusPartnerBackend.modal.request.team.TeamJoinRequest;
import com.example.CampusPartnerBackend.modal.request.team.TeamQuitRequest;
import com.example.CampusPartnerBackend.modal.request.team.TeamUpdateRequest;
import com.example.CampusPartnerBackend.modal.vo.TeamUserVO;
import com.example.CampusPartnerBackend.service.TeamService;
import com.example.CampusPartnerBackend.service.UserService;
import com.example.CampusPartnerBackend.service.UserTeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
@Api(tags = "队伍相关接口")
@RestController
@RequestMapping("/team")
//@CrossOrigin(originPatterns = {"http://user.zhezi.online"}, allowCredentials = "true")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @ApiOperation(value = "添加队伍")
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        User loginUser = userService.getLoginUser(request);
        Long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }
    @ApiOperation(value = "删除队伍")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(deleteRequest.getId(),loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }
    @ApiOperation(value = "更新队伍")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }
    @ApiOperation(value = "根据获取脱敏队伍信息")
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 前端传入一些信息，可以查询到与这些信息相匹配的队伍信息
     * @param teamQuery
     * @return
     */
    @ApiOperation(value = "查询队伍")
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery,HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        //1.先查出队伍id列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,isAdmin);
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        //2.判断队伍是否已加入队伍,未登录捕获异常
        try {
            User loginUser = userService.getLoginUser(request);
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("userId",loginUser.getId());
            userTeamQueryWrapper.in("teamId",teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            //已加入的队伍id集合
            Set<Long> hasJoinTeamSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            //遍历队伍列表，设置是否加入属性
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });

        } catch (Exception e) {}
        //3，队伍已加入用户数
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        Map<Long, List<UserTeam>> teamIdUserTeamList  = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> {
            team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(),new ArrayList<>()).size());
        });
        return ResultUtils.success(teamList);
    }

    @ApiOperation(value = "分页查询队伍")
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>(team);
        int current = teamQuery.getPageNum();
        int pageSize = teamQuery.getPageSize();
        Page<Team> page = new Page<>(current,pageSize);
        Page<Team> pageTeams = teamService.page(page, teamQueryWrapper);
        return ResultUtils.success(pageTeams);
    }
    @ApiOperation(value = "加入队伍")
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUSer = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest,loginUSer);
        return ResultUtils.success(result);
    }
    @ApiOperation(value = "退出队伍")
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if(teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result =  teamService.quitTeam(teamQuitRequest,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户已创建的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @ApiOperation(value = "获取当前用户已创建的队伍")
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery,HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(teamList);
    }
    @ApiOperation(value = "获取当前用户已加入的队伍")
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery,HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList idlist = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idlist);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(teamList);
    }
}
