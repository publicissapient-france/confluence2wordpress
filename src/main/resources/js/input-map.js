/*
 * Copyright 2011-2012 Alexandre Dutra
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
function InputMap(id, keyPrefix, valuePrefix, keyCss, valueCss, deleteText) {

	var self = this;
	this.id = id;
	this.keyPrefix = keyPrefix;
	this.valuePrefix = valuePrefix;
	this.deletePrefix = this.id + "-delete";
	this.deleteText = deleteText;
	this.keyCss = keyCss;
	this.valueCss = valueCss;
	
	this.showTable = function() {
		jQuery('#' + this.id).show();
	};
	
	this.hideTable = function() {
		jQuery('#' + this.id).hide();
	};
	
	this.deleteRow = function (rowIndex){
     	var rows = jQuery('#' + this.id + ' tbody tr:gt('+rowIndex+')');
     	jQuery('#' + this.id + ' tbody tr:eq('+rowIndex+')').remove();
     	rows.each(function(index){
 			var oldIndex = index + rowIndex + 1;
 			var newIndex = oldIndex - 1;
     		jQuery('input', jQuery(this)).each(function(){
     			var input = jQuery(this);
     			var newName = input.attr("name").replace('['+ oldIndex + ']', '['+ newIndex + ']');
     			input.attr("name", newName);
     		});
     		self.bindDeleteRow(newIndex);
     	});
     	if(jQuery('#' + this.id + ' tbody tr').length == 0){
     		this.hideTable();
     	}
     };

     this.addRow = function (key, value){
     	var rowIndex = jQuery('#' + this.id + ' tbody tr').length;
     	var html = 
     		'<tr>'+
     		'<td><input class="text '+this.keyCss+'" type="text" name="'+ this.keyPrefix +'['+ rowIndex +']" value="'+key+'" /></td>' +
	    	'<td><input class="text '+this.valueCss+'" type="text" name="'+ this.valuePrefix +'['+ rowIndex +']" value="'+value+'" /></td>' +
	    	'<td style="vertical-align:middle"><input name="'+ this.deletePrefix +'['+ rowIndex +']" class="button" type="button" style="margin:0" value="'+ this.deleteText +'" /></td>' +
	    	'</tr>';
     	if(rowIndex == 0){
     		jQuery('#' + this.id + ' tbody').html(html);
     	} else {
         	jQuery('#' + this.id + ' tbody tr:last').after(html);
     	}
     	this.bindDeleteRow(rowIndex);
     	this.showTable();
     };
 
     this.bindDeleteRow = function (rowIndex){
     	var deleteButton = jQuery("#" + this.id + " tbody input[name='"+ this.deletePrefix +"["+ rowIndex +"]']");
        deleteButton.unbind();
     	deleteButton.click(function(){
     		self.deleteRow(rowIndex);
     	});
 	};
 	
 	this.init = function(){
     	jQuery('#' + this.id + '-add').click(function(){
	    	self.addRow("", "");
	    });
	    this.hideTable();
    }
 	
};

         