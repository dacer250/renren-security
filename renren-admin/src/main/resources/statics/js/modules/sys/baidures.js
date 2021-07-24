$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/baidures/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '', name: 'fileId', index: 'file_id', width: 80,
        cellattr: function(rowId, tv, rawObject, cm, rdata) {
          //合并单元格
          return 'id=\'fileId' + rowId + "\'";
          //if (Number(rowId) < 5) { return ' colspan=2' }
        }},
			{ label: '', name: 'seq', index: 'seq', width: 80 }, 			
			{ label: '', name: 'name', index: 'name', width: 80 }, 			
			{ label: '', name: 'sex', index: 'sex', width: 80 }, 			
			{ label: '', name: 'idNo', index: 'id_no', width: 80 }, 			
			{ label: '', name: 'mobile', index: 'mobile', width: 80 }, 			
			{ label: '', name: 'extInfo', index: 'ext_info', width: 80 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
          var gridName = "jqGrid";
          Merger(gridName, 'fileId');
        }
    });
});
function Merger(gridName, CellName) {
  //得到显示到界面的id集合
  var mya = $("#" + gridName + "").getDataIDs();
  //当前显示多少条
  var length = mya.length;
  for (var i = 0; i < length; i++) {
    //从上到下获取一条信息
    var before = $("#" + gridName + "").jqGrid('getRowData', mya[i]);
    //定义合并行数
    var rowSpanTaxCount = 1;
    for (j = i + 1; j <= length; j++) {
      //和上边的信息对比 如果值一样就合并行数+1 然后设置rowspan 让当前单元格隐藏
      var end = $("#" + gridName + "").jqGrid('getRowData', mya[j]);
      if (before[CellName] == end[CellName]) {
        rowSpanTaxCount++;
        $("#" + gridName + "").setCell(mya[j], CellName, '', { display: 'none' });
      } else {
        rowSpanTaxCount = 1;
        break;
      }
      $("#" + CellName + "" + mya[i] + "").attr("rowspan", rowSpanTaxCount);
    }
  }
}
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		baiduRes: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.baiduRes = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
		    $('#btnSaveOrUpdate').button('loading').delay(1000).queue(function() {
                var url = vm.baiduRes.id == null ? "sys/baidures/save" : "sys/baidures/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.baiduRes),
                    success: function(r){
                        if(r.code === 0){
                             layer.msg("操作成功", {icon: 1});
                             vm.reload();
                             $('#btnSaveOrUpdate').button('reset');
                             $('#btnSaveOrUpdate').dequeue();
                        }else{
                            layer.alert(r.msg);
                            $('#btnSaveOrUpdate').button('reset');
                            $('#btnSaveOrUpdate').dequeue();
                        }
                    }
                });
			});
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			var lock = false;
            layer.confirm('确定要删除选中的记录？', {
                btn: ['确定','取消'] //按钮
            }, function(){
               if(!lock) {
                    lock = true;
		            $.ajax({
                        type: "POST",
                        url: baseURL + "sys/baidures/delete",
                        contentType: "application/json",
                        data: JSON.stringify(ids),
                        success: function(r){
                            if(r.code == 0){
                                layer.msg("操作成功", {icon: 1});
                                $("#jqGrid").trigger("reloadGrid");
                            }else{
                                layer.alert(r.msg);
                            }
                        }
				    });
			    }
             }, function(){
             });
		},
		getInfo: function(id){
			$.get(baseURL + "sys/baidures/info/"+id, function(r){
                vm.baiduRes = r.baiduRes;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});