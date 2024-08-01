package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public void add(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setStatus(1);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //创建的用户和更新的用户
        Long empId = BaseContext.getCurrentId();
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeMapper.add(employee);
    }

    @Override
    public PageResult page(EmployeePageQueryDTO dto) {
        //1.使用分页插件，设置查询第几页，多少条
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        //2。mapper
        Page<Employee> p = employeeMapper.page(dto);
        //3。封装结果
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public void updateStatus(int status, Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(status);

        employeeMapper.update(employee);
    }

    @Override
    public Employee findById(Long id) {
        Employee e = employeeMapper.findById(id);
        if(e == null){
            return null;
        }
        e.setPassword("****");
        return e;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee e = new Employee();
        BeanUtils.copyProperties(employeeDTO,e);
        e.setUpdateTime(LocalDateTime.now());
        e.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(e);
    }

}
