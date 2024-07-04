package cn.ybs.recyclerview.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.recyclerview.databinding.ActivityExampleCalendarBinding
import cn.ybs.recyclerview.databinding.ItemExampleCalendarBinding
import java.lang.StringBuilder
import java.util.Calendar
import java.util.Random

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/07/04
 *  desc   : 日历签到
 */
class ExampleCalendarActivity : BaseViewBindingActivity<ActivityExampleCalendarBinding>() {

    private var calendar: Calendar = Calendar.getInstance()

    private var monthCnt = 0
    private var curYear = calendar.get(Calendar.YEAR)
    private var curMonth = calendar.get(Calendar.MONTH)

    private lateinit var adapter: ExampleCalendarAdapter
    override fun initRecyclerViewAfterViewCreated() {
        adapter = ExampleCalendarAdapter()
        requireViewBinding().apply {
            rvCalendar.adapter = adapter
            processSwitchMonthClick(0)
        }
    }

    override fun initListenersAfterViewCreated() {
        requireViewBinding().apply {
            btnNextMonth.setOnClickListener { processSwitchMonthClick(1) }
            btnPreviousMonth.setOnClickListener { processSwitchMonthClick(-1) }
        }
    }

    private fun processSwitchMonthClick(monthAmount: Int) {
        adapter.replaceWholeMonthData(generateDaysInMonthByCalendar(calendar, monthAmount))
        refreshDateView()
        checkSwitchMonthButtonNeedDisable()
    }

    private fun generateDaysInMonthByCalendar(calendar: Calendar, monthAmount: Int = 0): MutableList<NormalEntity> {
        val days = mutableListOf<NormalEntity>()

        monthCnt += monthAmount
        curMonth += monthAmount
        if (curMonth < 0) {
            curMonth = 11
            curYear--
        } else if (curMonth > 11) {
            curMonth = 0
            curYear++
        }

        calendar.set(Calendar.YEAR, curYear)
        calendar.set(Calendar.MONTH, curMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // offset the first day of the week
        for (i in 1 until calendar.get(Calendar.DAY_OF_WEEK)) days.add(NormalEntity("", false))
        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) days.add(NormalEntity(i.toString(), Random().nextBoolean()))
        return days
    }

    private fun refreshDateView() {
        val sb = StringBuilder()
        sb.append("year: ${calendar.get(Calendar.YEAR)}")
        sb.append(", month: ${calendar.get(Calendar.MONTH) + 1}")
        sb.append(", days: ${calendar.getActualMaximum(Calendar.DAY_OF_MONTH)}")
        requireViewBinding().tvDate.text = sb.toString()
    }

    private fun checkSwitchMonthButtonNeedDisable() {
        val view = requireViewBinding()
        view.btnNextMonth.isEnabled = monthCnt < 0
        view.btnPreviousMonth.isEnabled = monthCnt > -12
    }

    private class ExampleCalendarAdapter : RecyclerView.Adapter<ExampleCalendarAdapter.ExampleCalendarViewHolder>() {

        inner class ExampleCalendarViewHolder(val viewBinding: ItemExampleCalendarBinding) : RecyclerView.ViewHolder(viewBinding.root)

        private lateinit var data: MutableList<NormalEntity>

        fun replaceWholeMonthData(data: MutableList<NormalEntity>) {
            this.data = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleCalendarViewHolder {
            return ExampleCalendarViewHolder(ItemExampleCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: ExampleCalendarViewHolder, position: Int) {
            val item = data[position]
            holder.viewBinding.tvDate.text = item.day
            holder.viewBinding.vDot.visibility = if (item.check) View.VISIBLE else View.GONE
        }

    }

    private data class NormalEntity(val day: String, val check: Boolean)


}