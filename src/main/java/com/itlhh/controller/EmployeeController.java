package com.itlhh.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itlhh.common.R;
import com.itlhh.entity.Employee;
import com.itlhh.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @Author lhh
 * @Date 2022/1/12 20:40
 */
@Slf4j
@RequestMapping("/employee")
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Value("${autoPassword}")
    private String autoPassword;

    /**
     * 登录功能
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //判断账户密码是否为空
        if (employee.getUsername() == null || employee.getPassword() == null) {
            return R.error("账号或者密码不能为空");
        }
        //判断密码长度不小于六位
        if (employee.getPassword().length() < 6) {
            return R.error("密码长度不能小于6位");
        }


        //①. 将页面提交的密码password进行md5加密处理, 得到加密后的字符串
        String password = employee.getPassword();
        log.info("本次的明文:{}", password);
        String s = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("本次的密文:{}", s);

        //  ②. 根据页面提交的用户名username查询数据库中员工数据信息
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());

        Employee serviceOne = employeeService.getOne(lqw);

        // System.out.println(serviceOne);
        //③. 如果没有查询到, 则返回登录失败结果
        if (serviceOne == null) {
            return R.error("登录失败");
        }
        //④. 密码比对，如果不一致, 则返回登录失败结果
        if (!serviceOne.getPassword().equals(s)) {
            log.info("本次密码是:{}", serviceOne.getPassword());
            log.info("比对的密码是:{}", s);
            return R.error("登录失败");
        }

        //⑤. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (serviceOne.getStatus() == 0) {
            return R.error("账号已被禁用");
        }

        //⑥. 登录成功，将员工id存入Session, 并返回登录成功结果
        HttpSession session = request.getSession();
        session.setAttribute("employee", serviceOne.getId());
        return R.success(serviceOne);
    }

    /**
     * 退出功能
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中的用户id
        request.getSession().removeAttribute("employee");
        //返回结果
        return R.success("退出成功");
    }

    /**
     * 新增功能
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("开始新增");
        //设置默认密码123456,并对密码进行MD5码加密
        String s = DigestUtils.md5DigestAsHex(autoPassword.getBytes());//用"123456".getBytes() 写得太死,硬编码
        //将加密后的密码添加到emplpyee中
        employee.setPassword(s);

/*        //设置创建时间
        LocalDateTime localDateTime = LocalDateTime.now();
        employee.setCreateTime(localDateTime);

        //设置修改时间
        employee.setUpdateTime(localDateTime);
        //创建者id
        Long createUser = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(createUser);

        //修改者id
        employee.setUpdateUser(createUser);*/
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 伪静态
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
/*    @GetMapping("/page/{page}/{pageSize}/{name}")
    public R<Page> page2(int page,int pageSize,String name){
        log.info("当前page={},当前pageSize={},当前name={}",page,pageSize,name);
        return null;
    }*/

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("当前page={},当前pageSize={},当前name={}", page, pageSize, name);

        //. 构造分页条件
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //构建搜索条件 - name进行模糊匹配
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);
        //构建排序条件 - 更新时间倒序排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行分页查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        // log.info("收到请求了");
        // log.info(employee.toString());
/*        Long empId = (Long) request.getSession().getAttribute("employee");
        log.info("id={}",empId);

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);*/
        long id = Thread.currentThread().getId();
        log.info("线程id为:{}", id);

        employeeService.updateById(employee);

        return R.success("员工修改成功");
    }

    /**
     * 根据id查询信息,回显数据
     *
     * @param request
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(HttpServletRequest request, @PathVariable Long id) {
        //log.info("id={}",id);

        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }

        return R.error("没有查询到对应员工信息");
    }

}
