package com.rf.taskmodule.ui.tasklisting;

import android.content.Context;
import android.os.CountDownTimer;

import androidx.databinding.ObservableField;

import com.rf.taskmodule.data.model.response.config.AssigneeDetail;
import com.rf.taskmodule.data.model.response.config.CallToActions;
import com.rf.taskmodule.data.model.response.config.Contact;
import com.rf.taskmodule.data.model.response.config.Executor;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.DateTimeUtil;
import com.rf.taskmodule.utils.Log;
import com.rf.taskmodule.data.local.prefs.PreferencesHelper;
import com.rf.taskmodule.data.model.response.config.AssigneeDetail;
import com.rf.taskmodule.data.model.response.config.CallToActions;
import com.rf.taskmodule.data.model.response.config.Executor;
import com.rf.taskmodule.data.model.response.config.Task;
//import com.rf.taskmodule.Receiver.ServiceRestartReceiver;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.CommonUtils;
import com.rf.taskmodule.utils.DateTimeUtil;
import com.rf.taskmodule.utils.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AssignTaskViewModel {
    public final TaskItemViewModelListener mListener;
    public ObservableField<Boolean> isStageRelativeLayoutVisible = new ObservableField<>(false);
    public ObservableField<String> stageText = new ObservableField<>("");
    public ObservableField<Boolean> isStartTimeRelativeLayoutVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isEndTimeRelativeLayoutVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isFeetNumberRelativeLayoutVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isAmountRelativeLayoutVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isViewLineVisible = new ObservableField<>(false);
    public ObservableField<Boolean> isCallRelativeLayoutVisible = new ObservableField<>(false);

    public ObservableField<Boolean> isAssignedVisible = new ObservableField<>(false);
    public ObservableField<String> contactAssignedName = new ObservableField<>("");
    public ObservableField<String> contactAssignedMobile = new ObservableField<>("");

    public ObservableField<String> paymentText = new ObservableField<>(AppConstants.INR + " 0");
    public final Task task;
    public ObservableField<Boolean> paymentStatusVisible = new ObservableField<>(false);
    public ObservableField<String> paymentStatusText = new ObservableField<>("");
    public ObservableField<Boolean> countDownTimerVisible = new ObservableField<>(false);
    public ObservableField<String> countDownTimerText = new ObservableField<>("");
    public CountDownTimer countDownTimer;

    public ObservableField<String> clientTaskId = new ObservableField<>("");
    public ObservableField<String> startTime = new ObservableField<>("");
    public ObservableField<String> endTime = new ObservableField<>("");
    public ObservableField<String> startTimeLabel = new ObservableField<>("");
    public ObservableField<Boolean> clientTaskIdVisible = new ObservableField<>(false);
    public ObservableField<String> fleetNumber = new ObservableField<>("");
    public ObservableField<String> contactPersonRole = new ObservableField<>("");
    public ObservableField<String> contactPersonName = new ObservableField<>("");
    public ObservableField<String> contactPersonLabel = new ObservableField<>("");
    public ObservableField<String> contactMobile = new ObservableField<>("");
    public ObservableField<String> contactPersonImage = new ObservableField<>("");
    public ObservableField<String> contactAssignedImage = new ObservableField<>("");
    public ObservableField<Boolean> isDetailShow = new ObservableField<>(false);
    public ObservableField<String> referenceId = new ObservableField<>("");
    public ObservableField<Boolean> referenceIdVisible = new ObservableField<>(false);
    public ObservableField<String> labelReffranceId = new ObservableField<>("");
    public ObservableField<String> labelEndTime = new ObservableField<>("");
    public ObservableField<Boolean> highLightExpiry = new ObservableField<>(false);
    public ObservableField<Boolean> validExpiry = new ObservableField<>(false);
    public ObservableField<String> expiryText = new ObservableField<>("");

    public AssignTaskViewModel(Task task, TaskItemViewModelListener mListener, Context context, PreferencesHelper preferencesHelper, String categoryId, boolean fromAssignedToMe) {
        this.task = task;
        this.mListener = mListener;
        try {


            String taskId = CommonUtils.getAllowFieldLabelName("REFERENCE_ID", categoryId, preferencesHelper);
            if (!taskId.isEmpty()) {
                labelReffranceId.set(taskId);
            } else {
                labelReffranceId.set("Reference Id");
            }


            if (task.getReferenceId() != null && !task.getReferenceId().isEmpty()) {
                referenceId.set(task.getReferenceId());
                referenceIdVisible.set(true);
            } else {
                referenceIdVisible.set(false);
            }
            if (task.getTcfId() != null)
                isDetailShow.set(true);
            if (task.getEndTime() > 0 && task.getCurrentStage().getTerminal() != null && !task.getCurrentStage().getTerminal()) {
                if (CommonUtils.getHighLightExpiry(categoryId, preferencesHelper)) {
                    this.highLightExpiry.set(true);
                    if (System.currentTimeMillis() < task.getEndTime()) {
                        this.validExpiry.set(true);
                        this.expiryText.set("Valid");
                        if (validExpiry.get() && task.getTaskId() != null) {
                            // handle task's call to action for the SYSTEM executor to execute timer based tasks.
                            if (task.getCurrentStage() != null) {
                                List<CallToActions> callToActionList = task.getCurrentStage().getCallToActions();
                                if (callToActionList != null && !callToActionList.isEmpty()) {
                                    // get the actions which are performed by users only.
                                    List<CallToActions> systemCallToActions = CommonUtils.extractCallToActionsWithTypeTask(callToActionList, Executor.SYSTEM, AppConstants.TIMED);
                                    if (systemCallToActions.size() > 0 && !preferencesHelper.getTimerCallToAction().containsKey(task.getTaskId())) {
                                        if (systemCallToActions.get(0) != null &&
                                                systemCallToActions.get(0).getConditions().get(0).getProperties() != null &&
                                                systemCallToActions.get(0).getConditions().get(0).getProperties().containsKey(AppConstants.TIME)) {
                                            int timeOutInSec = Integer.parseInt(systemCallToActions.get(0).getConditions().get(0).getProperties().get(AppConstants.TIME));

                                            long counterEndTime = task.getCreatedAt() + timeOutInSec * 1000;
                                            Log.e("counterEndTime", "" + counterEndTime);
                                            if (counterEndTime > System.currentTimeMillis()) {
                                                Log.e("condition Pass", "" + System.currentTimeMillis());
                                                Random randomNumber = new Random();
                                                Map<String, Task> taskMap = new HashMap<>();
                                                task.setRequestCode(randomNumber.nextInt());
                                                taskMap.put(task.getTaskId(), task);
                                                preferencesHelper.setTimerCallToAction(taskMap);

//                                    Calendar calendar = Calendar.getInstance();
//                                    calendar.add(Calendar.SECOND, timeOutInSec);

       /*                                         CommonUtils.registerAlarm(counterEndTime, context.getApplicationContext(),
                                                        ServiceRestartReceiver.ACTION_CALL_TO_ACTOIN_TIMER, task.getRequestCode(), task.getTaskId());*/
                                            }

                                        }

                                    }

                                    if (systemCallToActions.size() > 0)
                                        countDownTimerVisible.set((true));
                                    else {
                                        countDownTimerVisible.set((false));
                                    }
                                    // timerTask = preferencesHelper.getPendingTime();
//                                    int timeOutInSec = Integer.parseInt(systemCallToActions.get(0).getConditions().get(0).getProperties().get(AppConstants.TIME));
//                        long previousTime = 0;
//                        if (preferencesHelper.getPendingTime().get(task.getTaskId()) != null)
//                            previousTime = preferencesHelper.getPendingTime().get(task.getTaskId());

                                    // long fTim = timeOutInSec - previousTime;
                                    long counterEndTime = task.getEndTime();
                                    if (counterEndTime > System.currentTimeMillis()) {
                                        long fTim = counterEndTime - System.currentTimeMillis();
                                        countDownTimer = new CountDownTimer(fTim, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                //  timerTask.put(task.getTaskId(), (timeOutInSec - millisUntilFinished / 1000));
                                                //  preferencesHelper.setPendingTimeForTask(timerTask);
                                                // Log.e("time", "" + timerTask.get(task.getTaskId()));
                                                // long secondleft = (millisUntilFinished / 1000);
//                                if (secondleft <= 0) {
//                                    LocalBroadcastManager.getInstance(context).sendBroadcast(
//                                            new Intent(AppConstants.ACTION_REFRESH_TASK_LIST));
//                                }
                                                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
                                                countDownTimerText.set(hms + " Left");
                                                // countDownTimerText.set(secondleft + " Sec Left");
                                            }

                                            public void onFinish() {
                                                countDownTimerText.set(" Time UP");
                                                CommonUtils.showLogMessage("e", "finish 1", "1");
                                                countDownTimerVisible.set((false));
                                                if (preferencesHelper.getPendingTime().containsKey(task.getTaskId())) {
                                                    preferencesHelper.getPendingTime().remove(task.getTaskId());
                                                }


                                            }
                                        };
                                        countDownTimer.start();
                                    }

                                }
                            }
                        }

                    } else {
                        this.validExpiry.set(false);
                        this.expiryText.set("Expired");
                    }
                }

            } else {
                this.validExpiry.set(false);
            }

            if (task.getContact() != null && task.getContact().getName() != null && !task.getContact().getName().equals("")) {
                isCallRelativeLayoutVisible.set(true);
                if (task.getContact() != null && task.getContact().getProfileImage() != null)
                    contactPersonImage.set(task.getContact().getProfileImage());
                contactPersonName.set(task.getContact().getName());
                contactMobile.set(task.getContact().getMobile());
                if (task.getContact().getMobile() != null) {
                    String mobile = task.getContact().getMobile();
                    contactMobile.set(mobile);
                }
                String pocLabel = CommonUtils.getAllowFieldLabelName(
                        "POINT_OF_CONTACT",
                        categoryId,
                        preferencesHelper
                );
                if (!pocLabel.isEmpty()) {
                    contactPersonLabel.set(pocLabel);
                } else {
                    contactPersonLabel.set("Point Of Contact");
                }
            } else
                if (task.getRequestedUser() != null) {
                isCallRelativeLayoutVisible.set(true);
                if (task.getRequestedUser() != null && task.getRequestedUser().getProfileImage() != null)
                    contactPersonImage.set(task.getRequestedUser().getProfileImage());
                contactPersonName.set(task.getRequestedUser().getName());
                contactMobile.set(task.getRequestedUser().getMobile());
                if (task.getRequestedUser().getMobile() != null) {
                    String mobile = task.getRequestedUser().getMobile();
                    contactMobile.set(mobile);
                }
                String pocLabel = CommonUtils.getRequestedByLabel(
                        categoryId,
                        preferencesHelper
                );
                if (!pocLabel.isEmpty()) {
                    contactPersonLabel.set(pocLabel);
                } else {
                    contactPersonLabel.set("Client Details");
                }
            } else
                if (task.getAssigneeDetail() != null) {
                isCallRelativeLayoutVisible.set(true);
                if (task.getAssigneeDetail() != null && task.getAssigneeDetail().getProfileImage() != null)
                    contactPersonImage.set(task.getAssigneeDetail().getProfileImage());
                contactPersonName.set(task.getAssigneeDetail().getName());
                contactMobile.set(task.getAssigneeDetail().getMobile());
                if (task.getAssigneeDetail().getMobile() != null) {
                    String mobile = task.getAssigneeDetail().getMobile();
                    contactMobile.set(mobile);
                }
                contactPersonLabel.set("Created By");
            } else {
                isCallRelativeLayoutVisible.set(false);
            }

            // Assigned To Details
            if (task.getAssignedToDetails() != null) {
                if (task.getAssignedToDetails().size() > 0) {
                    if (task.getAssignedToDetails() != null) {
                        isAssignedVisible.set(true);
                        if (task.getAssignedToDetails() != null && task.getAssignedToDetails().get(0).getProfileImage() != null)
                            contactAssignedImage.set(task.getAssignedToDetails().get(0).getProfileImage());
                        if (task.getAssignedToDetails().get(0).getName() != null)
                            contactAssignedName.set(task.getAssignedToDetails().get(0).getName());
                        if (task.getAssignedToDetails().get(0).getMobile() != null) {
                            String mobile = task.getAssignedToDetails().get(0).getMobile();
                            contactAssignedMobile.set(mobile);
                        }
                    } else {
                        isAssignedVisible.set(false);
                    }
                } else {
                    isAssignedVisible.set(false);
                }
            } else {
                isAssignedVisible.set(false);
            }


//            if (fromAssignedToMe) {
//                if (task.getRequestedUser() != null && task.getRequestedUser().getMobile() != null && !task.getRequestedUser().getMobile().isEmpty()) {
//                    if (task.getRequestedUser() != null && task.getRequestedUser().getProfileImage() != null)
//                        contactPersonImage.set(task.getRequestedUser().getProfileImage());
//                    isCallRelativeLayoutVisible.set(true);
//                    if (task.getRequestedUser().getMobile() != null) {
//                        contactMobile.set(task.getRequestedUser().getMobile());
//                    }
//                    if (task.getRequestedUser().getName() != null) {
//                        contactPersonName.set(task.getRequestedUser().getName());
//                    }
//                } else {
//                    isCallRelativeLayoutVisible.set(false);
//                }
//            } else {
//                if (task.getAssignmentType() != null && task.getAssignmentType().equals("USER") && !task.getAssignedToDetails().isEmpty()) {
//                    CommonUtils.showLogMessage("e", "task.getAssignmentType()", task.getAssignmentType());
//                    AssigneeDetail assigneeDetail = task.getAssignedToDetails().get(0);
//                    if (assigneeDetail != null) {
//                        isCallRelativeLayoutVisible.set(true);
//                        contactPersonRole.set("Executive");
//                        if (assigneeDetail.getProfileImage() != null)
//                            contactPersonImage.set(assigneeDetail.getProfileImage());
//                        if (assigneeDetail.getMobile() != null) {
//                            contactMobile.set(assigneeDetail.getMobile());
//
//                        }
//                        if (assigneeDetail.getName() != null) {
//                            contactPersonName.set(assigneeDetail.getName());
//                        }
//
//                    } else {
//                        isCallRelativeLayoutVisible.set(false);
//                    }
//                }
//            }
            if (task.getClientTaskId() != null && !task.getClientTaskId().isEmpty()) {
                clientTaskIdVisible.set(true);
                clientTaskId.set(task.getClientTaskId());
            } else {
                clientTaskIdVisible.set(false);
            }

            if (task.getStartTime() != 0L) {
                String date = DateTimeUtil.getParsedDate(task.getStartTime());
                String time = DateTimeUtil.getParsedTime(task.getStartTime());
                this.startTime.set(date + " , " + time);
                String startTime = CommonUtils.getAllowFieldLabelName("START_TIME", categoryId, preferencesHelper);
                if (!startTime.isEmpty()) {
                    startTimeLabel.set(startTime);
                } else {
                    startTimeLabel.set("Start Time");
                }
//                this.startTimeLabel.set("Start Time");
                this.isStartTimeRelativeLayoutVisible.set(true);
            } else if (task.getCreatedAt() != 0L) {
                String date = DateTimeUtil.getParsedDate(task.getCreatedAt());
                String time = DateTimeUtil.getParsedTime(task.getCreatedAt());
                this.startTime.set(date + " , " + time);
                this.startTimeLabel.set("Created At");
                this.isStartTimeRelativeLayoutVisible.set(true);
            } else {
                this.isStartTimeRelativeLayoutVisible.set(false);
            }
            if (task.getEndTime() != 0L) {
                String date = DateTimeUtil.getParsedDate(task.getEndTime());
                String time = DateTimeUtil.getParsedTime(task.getEndTime());
                String endTime = CommonUtils.getAllowFieldLabelName("END_TIME", categoryId, preferencesHelper);
                if (!endTime.isEmpty()) {
                    labelEndTime.set(endTime);
                } else {
                    labelEndTime.set("End TIme");
                }
                this.endTime.set(date + " , " + time);
//                this.endTime.set(DateTimeUtil.getParsedDate(task.getEndTime()) + " , " + DateTimeUtil.getParsedTime(task.getEndTime()));
                this.isEndTimeRelativeLayoutVisible.set(true);
            } else {
                this.isEndTimeRelativeLayoutVisible.set(false);
            }
            if (task.getFleetDetail() != null && task.getFleetDetail().getRegNumber() != null
                    && !task.getFleetDetail().getRegNumber().isEmpty()) {
                this.fleetNumber.set(task.getFleetDetail().getRegNumber());
                this.isFeetNumberRelativeLayoutVisible.set(true);

            } else {
                this.isFeetNumberRelativeLayoutVisible.set(false);
            }
            if (task.getCurrentStage() != null && task.getCurrentStage().getName() != null && !task.getCurrentStage().getName().isEmpty()) {
                isStageRelativeLayoutVisible.set(true);
                stageText.set(task.getCurrentStage().getName());
            } else {
                isStageRelativeLayoutVisible.set(false);
            }
            if (task.getPaymentData() != null && task.getPaymentData().getAmountBreakup() != null && task.getPaymentData().getAmountBreakup().getTotalAmt() != null) {
                isAmountRelativeLayoutVisible.set(true);
                paymentText.set(task.getPaymentData().getAmountBreakup().getTotalAmt() + " INR");
                if (task.getPaymentData().getStatus() != null && !task.getPaymentData().getStatus().isEmpty()) {
                    paymentStatusVisible.set(true);
                    paymentStatusText.set(task.getPaymentData().getStatus());
                }
            } else {
                isAmountRelativeLayoutVisible.set(false);
            }
            if (isAmountRelativeLayoutVisible.get() || isCallRelativeLayoutVisible.get()) {
                isViewLineVisible.set(true);
            } else {
                isViewLineVisible.set(false);
            }

        } catch (Exception e) {

        }
    }

    public void onCallClick() {
        mListener.onCallClick(contactMobile.get());
    }

    public void onCallAssignedClick() {
        mListener.onCallClick(contactAssignedMobile.get());
    }

    public void onItemClick() {
        mListener.onItemClick(task);
    }

    public void onDetailsClick() {
        mListener.onDetailsClick(task);
    }

    public interface TaskItemViewModelListener {

        void onItemClick(Task bean);

        void onCallClick(String mobile);


        void onDetailsClick(Task task);
    }

}
