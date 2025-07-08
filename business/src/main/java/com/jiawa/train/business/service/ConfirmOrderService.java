package com.jiawa.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.config.BusinessApplication;
import com.jiawa.train.business.domain.*;
import com.jiawa.train.business.enums.ConfirmOrderStatusEnum;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.mapper.ConfirmOrderMapper;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.req.ConfirmOrderQueryReq;
import com.jiawa.train.business.req.ConfirmOrderTicketReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.concurrent.TimeUnit;

@Service
public class ConfirmOrderService {

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;


    @Autowired
    private StringRedisTemplate redisTemplate;



     @Autowired
     private RedissonClient redissonClient;



    public void save(ConfirmOrderDoReq req) {

        DateTime now = DateTime.now();


        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);

        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);

        }

        //confirmOrderMapper.insert(confirmOrder);

    }


    public PageResp<ConfirmOrderQueryResp> querList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria  = confirmOrderExample.createCriteria();


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrdersList = confirmOrderMapper.selectByExample(confirmOrderExample);


        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrdersList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrdersList, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }




    public void doConfirm(ConfirmOrderDoReq confirmOrderSaveReq) {
        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否再有效期内，tickets条数>0，同乘客同车次是否已买过票

        // 获取分布式锁
        String key = DateUtil.formatDate(confirmOrderSaveReq.getDate()) + "-" + confirmOrderSaveReq.getTrainCode();
        // setIfAbsent就是对应redis分布式锁的setnx方法
//        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(key, key, 10, TimeUnit.SECONDS);
//
//
//            if (Boolean.TRUE.equals(setIfAbsent)) {
//                LOG.info("恭喜，抢到锁了！lockKey：{}", key);
//            } else {
//                // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
//                 LOG.info("很遗憾，没抢到锁！lockKey：{}", key);
//                 throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//
//    //            LOG.info("没抢到锁，有其它消费线程正在出票，不做任何处理");
//    //            return;
//            }

        RLock lock = null;
        try {

            // 使用redisson，自带看门狗
            lock = redissonClient.getLock(key);

            // 红锁的写法
            // RedissonRedLock redissonRedLock = new RedissonRedLock(lock, lock, lock);
            // boolean tryLock1 = redissonRedLock.tryLock(0, TimeUnit.SECONDS);

            /**
             waitTime – the maximum time to acquire the lock 等待获取锁时间(最大尝试获得锁的时间)，超时返回false
             leaseTime – lease time 锁时长，即n秒后自动释放锁
             time unit – time unit 时间单位
             */
            // boolean tryLock = lock.tryLock(30, 10, TimeUnit.SECONDS); // 不带看门狗
            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS); // 带看门狗
            if (tryLock) {
                LOG.info("恭喜，抢到锁了！");
                // 可以把下面这段放开，只用一个线程来测试，看看redisson的看门狗效果
                // for (int i = 0; i < 30; i++) {
                //     Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
                //     LOG.info("锁过期时间还有：{}", expire);
                //     Thread.sleep(1000);
                // }


            } else {
                // 只是没抢到锁，并不知道票抢完了没，所以提示稍候再试
                LOG.info("很遗憾，没抢到锁");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }


            Date date = confirmOrderSaveReq.getDate();
            String trainCode = confirmOrderSaveReq.getTrainCode();
            String start = confirmOrderSaveReq.getStart();
            String end = confirmOrderSaveReq.getEnd();
            List<ConfirmOrderTicketReq> ticketReqs = confirmOrderSaveReq.getTickets();

            // 保存确认订单，状态初始
            DateTime now = DateTime.now();

            ConfirmOrder confirmOrder = new ConfirmOrder();
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setMemberId(LoginMemberContext.getId());
            confirmOrder.setDate(date);
            confirmOrder.setTrainCode(trainCode);
            confirmOrder.setStart(start);
            confirmOrder.setEnd(end);
            confirmOrder.setDailyTrainTicketId(confirmOrderSaveReq.getDailyTrainTicketId());
            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrder.setTickets(JSON.toJSONString(ticketReqs));

            confirmOrderMapper.insert(confirmOrder);


            // 查余票记录，需要得到真实的库存
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
            LOG.info("查出余票记录：{}", dailyTrainTicket);


            //扣减余票数量，并判断余票是否足够
            reduceTickets(confirmOrderSaveReq, dailyTrainTicket);

            // 最终的选座结果
            List<DailyTrainSeat> finalSeatList = new ArrayList<>();

            // 计算相对第一个座位的偏离值
            // 比如选择的是c1、d2，则偏移量为：[0, 5]
            // 比如选择的是a1、b1、c1，则偏移量为：[0, 1, 2]
            ConfirmOrderTicketReq ticketReq0 = ticketReqs.get(0);
            LOG.info("ticketReq0是{}", ticketReq0);
            if (StrUtil.isNotBlank(ticketReq0.getSeat())) {
                LOG.info("本次购票有选座");
                // 查出本次选座的座位类型都有那些列，用于计算所选座位与第一个座位的偏离值
                List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
                LOG.info("本次选座的座位类型包含的列：{}", colEnumList);

                // 组成和前端两拍选座一样的列表，用于作参照的座位列表。例如：referSeatList = {A1, C1, D1, F1, A2, C2, D2, F2}
                List<String> referSeatList = new ArrayList<>();

                for (int i = 1; i <= 2; i++) {
                    for (SeatColEnum seatColEnum : colEnumList) {
                        referSeatList.add(seatColEnum.getCode() + i);
                    }
                }
                LOG.info("用于做参照的两排座位");

                // 绝对偏移值，即：在操作座位列表中的位置
                List<Integer> aboluteOffsetList = new ArrayList<>();
                for (ConfirmOrderTicketReq ticket : ticketReqs) {
                    int index = referSeatList.indexOf(ticket.getSeat());
                    aboluteOffsetList.add(index);
                }
                LOG.info("计算得到所有座位的绝对偏移值：{}", aboluteOffsetList);

                List<Integer> offsetList = new ArrayList<>();
                for (Integer index : aboluteOffsetList) {
                    int offeset = index - aboluteOffsetList.get(0);
                    offsetList.add(offeset);
                }
                LOG.info("计算得到所有座位的相对第一个座位移值：{}", offsetList);


                getSeat(finalSeatList,
                        date,
                        trainCode,
                        ticketReq0.getSeatTypeCode(),
                        ticketReq0.getSeat().split("")[0],  // 从A1得到A
                        offsetList,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex());


            } else {
                LOG.info("本次购票没有选座");

                for (ConfirmOrderTicketReq ticket : ticketReqs) {
                    getSeat(finalSeatList,
                            date,
                            trainCode,
                            ticket.getSeatTypeCode(),
                            null,
                            null,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex());
                }

            }

            LOG.info("最终的选座：{}", finalSeatList);
//
//        // 选中座位后的事务处理
//        // 座位表修改售卖情况sell；
//        // 余票详情表修改余票；
//        // 为会员增加购票记录；
//        // 更新确认订单为成功。
//
            try {
                afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, ticketReqs, confirmOrder);
            } catch (Exception e) {
                LOG.error("保存购票信息失败");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
            }

            //LOG.info("购票流程结束，释放锁！lockKey：{}", key);
            //redisTemplate.delete(key);
        } catch (InterruptedException e) {
            LOG.error("购票异常", e);
        } finally {
            LOG.info("购票流程结束，释放锁！");
            if (null != lock && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


    }

    /**
     * 挑座位，如果有选座，则一次性挑完，如果无选座，则一个一个挑
     * @param date
     * @param trainCode
     * @param seatType
     * @param column
     * @param offsetList
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList
            , Integer startIndex, Integer endIndex) {
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢",carriageList.size());

        // 一个车厢一个车厢获取座位数据

        for (DailyTrainCarriage dailyTrainCarriage: carriageList) {
            LOG.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());

            getSeatList = new ArrayList<>();
            List<DailyTrainSeat> seatList =
                    dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());

            LOG.info("车厢{}的座位数:{}", dailyTrainCarriage.getIndex(), seatList.size());
            for (int i = 0; i < seatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat = seatList.get(i);
                String col = dailyTrainSeat.getCol();
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();

                // 判断当前座位有没有被选中过：主要为了不选座位时，多用户选中同一个座位
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSet: finalSeatList) {
                    if (finalSet.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    LOG.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }

                // 判断column，有值的话就去比对列号
                if (StrUtil.isBlank(column)) {
                    LOG.info("无选座");
                } else {
                    if (!column.equals(col)) {
                        LOG.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }


                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    LOG.info("未选中座位");
                    continue;
                }

                // 根据offset选剩下的座位
                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    LOG.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);

                    // 从索引1开始，索引0就是当前已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        // 索引在数据库中从1开始，Java以0开始
//                        int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset;

                        // 有选座时，一定是在同一个车厢
                        if (nextIndex >= seatList.size()) {
                            LOG.info("座位{}不可选，偏移后的索引超出了这个车厢的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            getSeatList.add(nextDailyTrainSeat);
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                        } else {
                            LOG.info("座位{}不可选，已被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }

                if (!isGetAllOffsetSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }

                // 保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;


            }

        }

    }

    /**
     * 计算某座位在区间内可买
     * 例子：sell = 10001，本次购买区间站 1~4, 则区间已售 000
     * 全都是0，表示这个区间可买，只要有1，就表示区间内已售过票
     *
     * 选中后，要计算购票后的sell，比如原来是1000110101，本次购买区间为 1~4 站
     * 方案：狗营造本次购票造成的售卖信息0111000000，和原sell 10001 按位与。最终得到111111
     */

    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        // 10001
        String sell = dailyTrainSeat.getSell();

        LOG.info("啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊：{}", sell);
        String sellPart = sell.substring(startIndex, endIndex);


        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}~{}已销售，不可选中座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            LOG.info("座位{}在本次车站区间{}~{}未销售，可正常选票", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            // 111
            String curSell = sellPart.replace('0', '1');
            // 0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息与库里的已售信息按位与，即可得到该座位卖出此票后的售票情况
            // 32
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            // 11111
            String newSell = NumberUtil.getBinaryStr(newSellInt);

            newSell = StrUtil.fillBefore(newSell, '0', sell.length());

            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;
        }
    }


    private static void reduceTickets(ConfirmOrderDoReq confirmOrderSaveReq, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : confirmOrderSaveReq.getTickets()) {
            String typeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, typeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }


}
