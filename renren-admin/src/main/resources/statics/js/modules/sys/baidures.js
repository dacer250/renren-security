$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/baidures/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '图片ID', name: 'fileId', index: 'fileId', width: 80
        },
          { label: '', name: 'url', index: 'url', width: 40 ,visible:false  },
          { label: '', name: 'seq', index: 'seq', width: 50 },
			{ label: '', name: 'name', index: 'name', width: 80 }, 			
			{ label: '', name: 'sex', index: 'sex', width: 20 },
			{ label: '', name: 'idNo', index: 'id_no', width: 100 },
			{ label: '', name: 'mobile', index: 'mobile', width: 80 },
          { label: '', name: 'checked', index: 'checked', width: 80, formatter: function(value, options, row){
              return value === 1 ?
                  '<span class="label label-success">校对OK</span>' :
                  '<span class="label label-danger">未校对</span>';
            }} ,
          //{ label: '', name: 'extInfo', index: 'ext_info', width: 80 ,visible:false}
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
          //Merger(gridName, 'fileId');
          //Merger(gridName, 'url');
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
    $("#" + CellName + "" + mya[length-1] + "").click(function (){
       vm.showPic()
    });

  }
}
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		baiduRes: {},
    cur: 0,
	},
	methods: {
		query: function () {
			vm.reload();
		},
    showPic: function (url) {
      layer.open({
        type: 2,
        title: false,
        area: ['806px', '467px'],
        closeBtn: 1,
        shadeClose: false,
        content: [url, 'no']
      });
    },
    rot:function(){
      vm.cur = (vm.cur+90)%360;
      document.getElementById('imgRet').style.transform = 'rotate('+vm.cur+'deg)';
    },
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			var temp =vm.baiduRes;
			var fid =vm.baiduRes.fileId;
      vm.baiduRes = {fileId:fid,url:temp.url,seq:'',name:'',sex:'',idNo:'',mobile:''};
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
		    $('#btnSaveOrUpdate').button('loading').delay(20).queue(function() {
                var url = vm.baiduRes.id == null ? "sys/baidures/save" : "sys/baidures/update";
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.baiduRes),
                    success: function(r){
                        if(r.code === 0){
                             layer.msg("操作成功", {icon: 1});
                             if(url==="sys/baidures/save"){
                               $('#btnSaveOrUpdate').button('reset');
                               $('#btnSaveOrUpdate').dequeue();
                               vm.reload();
                             }else{
                               $('#btnSaveOrUpdate').button('reset');
                               $('#btnSaveOrUpdate').dequeue();
                               vm.getNextInfo(vm.baiduRes.id)
                             }
                             //vm.reload();


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
    getNextInfo: function(id){
      $.get(baseURL + "sys/baidures/Nextinfo/"+id, function(r){
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