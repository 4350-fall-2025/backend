/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [1.0, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Login Owner"], "isController": false}, {"data": [1.0, 500, 1500, "Get All Pets"], "isController": false}, {"data": [1.0, 500, 1500, "Create Pet"], "isController": false}, {"data": [1.0, 500, 1500, "Delete Pet"], "isController": false}, {"data": [1.0, 500, 1500, "Login Vet"], "isController": false}, {"data": [1.0, 500, 1500, "Update Owner"], "isController": false}, {"data": [1.0, 500, 1500, "Delete Owner"], "isController": false}, {"data": [1.0, 500, 1500, "Vets Signup"], "isController": false}, {"data": [1.0, 500, 1500, "Get Pet"], "isController": false}, {"data": [1.0, 500, 1500, "Delete Vet"], "isController": false}, {"data": [1.0, 500, 1500, "Update Vet"], "isController": false}, {"data": [1.0, 500, 1500, "Get Owner"], "isController": false}, {"data": [1.0, 500, 1500, "Owners Signup"], "isController": false}, {"data": [1.0, 500, 1500, "Get Vet"], "isController": false}, {"data": [1.0, 500, 1500, "Update Pet"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2250, 0, 0.0, 2.399111111111112, 0, 234, 2.0, 4.0, 6.0, 10.489999999999782, 224.97750224977503, 90.67940862163785, 58.76365488451155], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Login Owner", 150, 0, 0.0, 2.3200000000000003, 1, 11, 2.0, 3.0, 4.0, 8.450000000000045, 29.78554408260524, 11.634978157267673, 7.708173029189834], "isController": false}, {"data": ["Get All Pets", 150, 0, 0.0, 1.1266666666666671, 0, 6, 1.0, 2.0, 2.0, 4.470000000000027, 30.168946098149636, 15.17285863334674, 6.069143453338696], "isController": false}, {"data": ["Create Pet", 150, 0, 0.0, 3.5466666666666664, 1, 34, 3.0, 5.0, 6.0, 22.27000000000021, 29.946097025354362, 15.002292748053502, 12.39955579956079], "isController": false}, {"data": ["Delete Pet", 150, 0, 0.0, 2.9466666666666663, 1, 16, 3.0, 4.0, 5.0, 13.450000000000045, 30.23583954847813, 11.279385456561176, 5.964491785930256], "isController": false}, {"data": ["Login Vet", 150, 0, 0.0, 1.6599999999999997, 0, 7, 1.0, 3.0, 3.0, 6.490000000000009, 29.928172386272944, 12.53826753292099, 7.686630212490025], "isController": false}, {"data": ["Update Owner", 150, 0, 0.0, 1.4666666666666666, 0, 5, 1.0, 2.0, 3.0, 4.490000000000009, 30.223655047350395, 11.274840066492041, 8.028158371952449], "isController": false}, {"data": ["Delete Owner", 150, 0, 0.0, 1.3199999999999994, 0, 5, 1.0, 2.0, 3.0, 5.0, 30.278562777553493, 5.647661611828825, 6.032057428340735], "isController": false}, {"data": ["Vets Signup", 150, 0, 0.0, 8.67333333333333, 1, 234, 5.0, 8.900000000000006, 16.0, 201.87000000000057, 30.587275693311582, 14.158563162724308, 10.36502408747961], "isController": false}, {"data": ["Get Pet", 150, 0, 0.0, 0.693333333333333, 0, 2, 1.0, 1.0, 1.4499999999999886, 2.0, 30.205396697543293, 15.132195806484091, 5.86999408477648], "isController": false}, {"data": ["Delete Vet", 150, 0, 0.0, 1.226666666666667, 0, 4, 1.0, 2.0, 2.0, 3.490000000000009, 30.296909715209047, 5.651083745707938, 5.976538830539285], "isController": false}, {"data": ["Update Vet", 150, 0, 0.0, 1.5266666666666666, 0, 4, 1.0, 2.0, 3.0, 3.490000000000009, 30.211480362537767, 12.184903700906345, 7.965917673716012], "isController": false}, {"data": ["Get Owner", 150, 0, 0.0, 0.7866666666666668, 0, 5, 1.0, 1.0, 2.0, 3.980000000000018, 30.156815440289506, 11.780006031363088, 5.919453030759952], "isController": false}, {"data": ["Owners Signup", 150, 0, 0.0, 5.253333333333335, 2, 31, 4.0, 8.900000000000006, 10.0, 25.90000000000009, 32.09242618741977, 14.009096197047496, 9.872181883825416], "isController": false}, {"data": ["Get Vet", 150, 0, 0.0, 0.64, 0, 3, 1.0, 1.0, 1.0, 2.490000000000009, 30.168946098149636, 12.639138550884955, 5.862910423370876], "isController": false}, {"data": ["Update Pet", 150, 0, 0.0, 2.8000000000000007, 1, 8, 3.0, 4.0, 5.0, 6.980000000000018, 30.223655047350395, 15.111827523675196, 13.075272642554905], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 2250, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
