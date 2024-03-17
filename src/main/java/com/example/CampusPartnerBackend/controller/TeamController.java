package com.example.CampusPartnerBackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.CampusPartnerBackend.Mapper.TeamMapper;
import com.example.CampusPartnerBackend.common.BaseResponse;
import com.example.CampusPartnerBackend.common.ErrorCode;
import com.example.CampusPartnerBackend.common.ResultUtils;
import com.example.CampusPartnerBackend.constant.UserConstant;
import com.example.CampusPartnerBackend.exception.BusinessException;
import com.example.CampusPartnerBackend.modal.domain.Team;
import com.example.CampusPartnerBackend.modal.domain.User;
import com.example.CampusPartnerBackend.modal.dto.TeamQuery;
import com.example.CampusPartnerBackend.modal.request.TeamAddRequest;
import com.example.CampusPartnerBackend.modal.request.TeamJoinRequest;
import com.example.CampusPartnerBackend.modal.request.TeamUpdateRequest;
import com.example.CampusPartnerBackend.modal.vo.TeamUserVO;
import com.example.CampusPartnerBackend.service.TeamService;
import com.example.CampusPartnerBackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/team")
@CrossOrigin(originPatterns = {"http://localhost:3000"}, allowCredentials = "true", allowedHeaders = {"*"})
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

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

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request) {
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
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery,HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,isAdmin);
        return ResultUtils.success(teamList);
    }

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

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest,HttpServletRequest request){
        if(teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUSer = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest,loginUSer);
        return ResultUtils.success(result);
    }


}
