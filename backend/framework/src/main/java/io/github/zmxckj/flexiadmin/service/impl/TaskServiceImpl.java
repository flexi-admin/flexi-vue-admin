package io.github.zmxckj.flexiadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zmxckj.flexiadmin.entity.Task;
import io.github.zmxckj.flexiadmin.mapper.TaskMapper;
import io.github.zmxckj.flexiadmin.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
}