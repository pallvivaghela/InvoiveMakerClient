package com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ClientReportAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Adapter.ReportAdapter
import com.billcreator.invoice.invoicegenerator.invoicemaker.Database.LoadDatabase.Companion.instance
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.ClientReportDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.Dto.MonthlyReportDTO
import com.billcreator.invoice.invoicegenerator.invoicemaker.R
import com.billcreator.invoice.invoicegenerator.invoicemaker.utils.NonScrollListView
import java.util.*

class ReportFragment : Fragment(), View.OnClickListener {
    var client1: TextView? = null
    var client2: CardView? = null
    var clientReportAdapter: ClientReportAdapter? = null
    private var clientsNumber: TextView? = null
    var listData: ArrayList<MonthlyReportDTO>? = null
    var listDataClient: ArrayList<ClientReportDTO>? = null
    var listReport: NonScrollListView? = null
    private var listTitle: LinearLayout? = null
    private var mActivity: Activity? = null
    var paid1: TextView? = null
    var paid2: CardView? = null
    var reportAdapter: ReportAdapter? = null
    private lateinit var spinnerData: Array<String?>
    private var vview: View? = null
    private var yearlyClientReport: TreeMap<Int, ArrayList<ClientReportDTO>>? = null
    private var yearlyReport: TreeMap<Int, ArrayList<MonthlyReportDTO>>? = null
    private var yearlyReportSpinner: Spinner? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        if (vview == null) {
            vview = layoutInflater.inflate(R.layout.report_fragment_layout, viewGroup, false)
            setUpData()
            setUpPaidListData()
            initLayout()
            setUpSpinner()
        }
        return vview
    }

    @SuppressLint("WrongConstant")
    private fun initLayout() {
        yearlyReportSpinner = vview!!.findViewById<View>(R.id.yearly_report) as Spinner
        listTitle = vview!!.findViewById<View>(R.id.list_title) as LinearLayout
        clientsNumber = vview!!.findViewById<View>(R.id.clients_number) as TextView
        listReport = vview!!.findViewById<View>(R.id.list_report) as NonScrollListView
        val reportAdapter2 = ReportAdapter(mActivity!!, listData!!)
        reportAdapter = reportAdapter2
        listReport!!.adapter = reportAdapter2
        if (listReport!!.adapter.count <= 0) {
            listTitle!!.visibility = 8
        }
        client1 = vview!!.findViewById<View>(R.id.client_1) as TextView
        client2 = vview!!.findViewById<View>(R.id.client_2) as CardView
        paid1 = vview!!.findViewById<View>(R.id.paid_1) as TextView
        paid2 = vview!!.findViewById<View>(R.id.paid_2) as CardView
        client1!!.setOnClickListener(this)
        client2!!.setOnClickListener(this)
        paid1!!.setOnClickListener(this)
        paid2!!.setOnClickListener(this)
    }

    private fun setUpData() {
        var z: Boolean
        val unPaidInvoices = instance!!.getUnPaidInvoices(2)
        yearlyReport = TreeMap(Collections.reverseOrder())
        yearlyClientReport = TreeMap(Collections.reverseOrder())
        val instance = Calendar.getInstance()
        for (i in unPaidInvoices.indices) {
            val catalogDTO = unPaidInvoices[i]
            instance.timeInMillis = catalogDTO.creationDate!!.toLong()
            var arrayList = yearlyReport!![Integer.valueOf(instance[1])]
            var arrayList2 = yearlyClientReport!![Integer.valueOf(instance[1])]
            if (arrayList == null) {
                arrayList = ArrayList()
                for (i2 in 0..11) {
                    arrayList.add(MonthlyReportDTO())
                }
            }
            if (arrayList2 == null) {
                arrayList2 = ArrayList()
            }
            val monthlyReportDTO = arrayList[instance[2]]
            monthlyReportDTO.totalInvoices = monthlyReportDTO.totalInvoices + 1
            val clientDTO = catalogDTO.clientDTO
            if (clientDTO == null || clientDTO.id <= 0) {
                z = false
            } else {
                monthlyReportDTO.setClients(clientDTO.id)
                var i3 = 0
                while (true) {
                    if (i3 >= arrayList2.size) {
                        break
                    } else if (arrayList2[i3].id == clientDTO.id) {
                        arrayList2[i3].invoices = arrayList2[i3].invoices + 1
                        arrayList2[i3].paidAmount =
                            arrayList2[i3].paidAmount + catalogDTO.totalAmount
                        break
                    } else {
                        i3++
                    }
                }
                val clientReportDTO = ClientReportDTO()
                clientReportDTO.id = clientDTO.id
                clientReportDTO.name = clientDTO.clientName
                clientReportDTO.invoices = 1
                clientReportDTO.paidAmount = catalogDTO.totalAmount.toDouble()
                arrayList2.add(clientReportDTO)
                z = true
            }
            if (!z) {
                var i4 = 0
                while (true) {
                    if (i4 >= arrayList2.size) {
                        break
                    } else if (arrayList2[i4].id == -1L) {
                        arrayList2[i4].invoices = arrayList2[i4].invoices + 1
                        arrayList2[i4].paidAmount =
                            arrayList2[i4].paidAmount + catalogDTO.totalAmount
                        z = true
                        break
                    } else {
                        i4++
                    }
                }
                if (!z) {
                    val clientReportDTO2 = ClientReportDTO()
                    clientReportDTO2.id = -1
                    clientReportDTO2.name = "(None)"
                    clientReportDTO2.invoices = 1
                    clientReportDTO2.paidAmount = catalogDTO.totalAmount.toDouble()
                    arrayList2.add(clientReportDTO2)
                }
            }
            monthlyReportDTO.totalPaidAmount =
                monthlyReportDTO.totalPaidAmount + catalogDTO.totalAmount
            yearlyReport!![Integer.valueOf(instance[1])] = arrayList
            yearlyClientReport!![Integer.valueOf(instance[1])] = arrayList2
        }
    }

    private fun setUpPaidListData() {
        listData = ArrayList()
        for ((key, value) in yearlyReport!!) {
            val monthlyReportDTO = MonthlyReportDTO()
            monthlyReportDTO.yearOrMonth = key
            var d = 0.0
            var i = 0
            var i2 = 0
            for (i3 in value.indices) {
                i += value[i3].totalInvoices
                i2 = (i2.toLong() + value[i3].totalClients).toInt()
                d += value[i3].totalPaidAmount
                value[i3].yearOrMonth = i3
            }
            monthlyReportDTO.totalInvoices = i
            monthlyReportDTO.setTotalClientsPerYear(i2)
            monthlyReportDTO.totalPaidAmount = d
            listData!!.add(monthlyReportDTO)
            for (size in value.indices.reversed()) {
                listData!!.add(value[size])
            }
        }
    }

    private fun setUpClientList() {
        listDataClient = ArrayList()
        for ((key, value) in yearlyClientReport!!) {
            val clientReportDTO = ClientReportDTO()
            clientReportDTO.id = -999
            clientReportDTO.name = key.toString()
            var d = 0.0
            var i = 0
            for (i2 in value.indices) {
                i += value[i2].invoices
                d += value[i2].paidAmount
            }
            clientReportDTO.invoices = i
            clientReportDTO.paidAmount = d
            listDataClient!!.add(clientReportDTO)
            listDataClient!!.addAll(value)
        }
    }

    @SuppressLint("ResourceType")
    private fun setUpSpinner() {
        var i = 1
        val strArr = arrayOfNulls<String>(yearlyReport!!.size + 1)
        spinnerData = strArr
        strArr[0] = "ALL"
        val it: Iterator<Map.Entry<Int, ArrayList<MonthlyReportDTO>>> =
            yearlyReport!!.entries.iterator()
        while (it.hasNext()) {
            val strArr2 = spinnerData
            strArr2[i] = it.next().key.toString() + ""
            i++
        }
        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(mActivity!!, 17367050, spinnerData)
        arrayAdapter.setDropDownViewResource(17367049)
        yearlyReportSpinner!!.adapter = arrayAdapter
        yearlyReportSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /* class com.billcreator.invoice.invoicegenerator.invoicemaker.Fragment.ReportFragment.AnonymousClass1 */

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}


            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, j: Long) {
                filterList(i, adapterView.getItemAtPosition(i).toString(), view)
                (adapterView.getChildAt(0) as TextView).setTextColor(-1)
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun filterList(i: Int, str: String, view2: View) {
        setUpData()
        if (view2.id == R.id.paid_1) {
            clientsNumber!!.visibility = 0
            if (i != 0) {
                val it: MutableIterator<Map.Entry<Int, ArrayList<MonthlyReportDTO>>> =
                    yearlyReport!!.entries.iterator()
                while (it.hasNext()) {
                    if (it.next().key != str.toInt()) {
                        it.remove()
                    }
                }
            }
            setUpPaidListData()
            val reportAdapter2 = ReportAdapter(mActivity!!, listData!!)
            reportAdapter = reportAdapter2
            listReport!!.adapter = reportAdapter2
        } else if (view2.id == R.id.client_1) {
            clientsNumber!!.visibility = 4
            if (i != 0) {
                val it2: MutableIterator<Map.Entry<Int, ArrayList<ClientReportDTO>>> =
                    yearlyClientReport!!.entries.iterator()
                while (it2.hasNext()) {
                    if (it2.next().key != str.toInt()) {
                        it2.remove()
                    }
                }
            }
            setUpClientList()
            val clientReportAdapter2 = ClientReportAdapter(mActivity!!, listDataClient!!)
            clientReportAdapter = clientReportAdapter2
            listReport!!.adapter = clientReportAdapter2
        }
        if (listReport!!.adapter.count <= 0) {
            listTitle!!.visibility = 8
        } else {
            listTitle!!.visibility = 0
        }
    }

    @SuppressLint("WrongConstant")
    override fun onClick(view2: View) {
        val selectedItemPosition = yearlyReportSpinner!!.selectedItemPosition
        filterList(
            selectedItemPosition,
            yearlyReportSpinner!!.getItemAtPosition(selectedItemPosition).toString(),
            view2
        )
        if (view2.id == R.id.client_1) {
            client1!!.visibility = 4
            client2!!.visibility = 0
            paid1!!.visibility = 0
            paid2!!.visibility = 4
        }
        if (view2.id == R.id.paid_1) {
            client1!!.visibility = 0
            client2!!.visibility = 4
            paid1!!.visibility = 4
            paid2!!.visibility = 0
        }
    }

    companion object {
        const val CLIENT_ITEM_TITLE: Long = -999
        const val NO_CLIENT_ASSIGNED: Long = -1
    }
}