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
/*
 * Autocomplete widget for comboboxes.
 * Based on: http://jqueryui.com/demos/autocomplete/#combobox
 */
jQuery.widget( "ui.autocompleteOnCombobox", {
	_create: function() {
		var self = this;
		var select = this.element.hide();
		var id = select.attr("id"), multiple = select.attr("multiple");
		var selected = select.children( ":selected" );
		if(multiple){
			var value = selected.map(function(){
				return this.text;
			})
			value = value.toArray().join(", ");
		} else {
			var value = selected.val() ? selected.text() : "";
		}
		var input = this.input = jQuery( "<input id='"+id+"_input'>" )
			.insertAfter( select )
			.val( value )
			.autocomplete({
				delay: 0,
				minLength: 0,
				source: function( request, response ) {
					var term = multiple ? request.term.split( /,\s*/ ).pop() : request.term;
					var matcher = new RegExp( jQuery.ui.autocomplete.escapeRegex(term), "i" );
					response( select.children( "option" ).map(function() {
						var text = jQuery( this ).text();
						if ( this.value && ( !term || matcher.test(text) ) )
							return {
								label: text.replace(
									new RegExp(
										"(?![^&;]+;)(?!<[^<>]*)(" +
										jQuery.ui.autocomplete.escapeRegex(term) +
										")(?![^<>]*>)(?![^&;]+;)", "gi"
									), "<strong>$1</strong>" ),
								value: text,
								option: this
							};
					}) );
				},
				select: function( event, ui ) {
					ui.item.option.selected = true;
					if(multiple){
						var terms = this.value.split( /,\s*/ );
		                // remove the current input
		                terms.pop();
		                // add the selected item
		                terms.push( ui.item.option.text );
		                // add placeholder to get the comma-and-space at the end
		                //terms.push( "" );
		                this.value = terms.join( ", " );
		                return false;	
					} else {
						self._trigger( "selected", event, {
							item: ui.item.option
						});
					}
				},
				change: function( event, ui ) {
					if ( !ui.item ) {
						if(multiple){
							select.children( "option" ).each(function() {
			                	this.selected = false;
							});
							var terms = this.value.split( /,\s*/ );
			                terms = terms.filter(function(element) {
			                	var valid = false;
								var matcher = new RegExp( "^" + jQuery.ui.autocomplete.escapeRegex( element ) + "$", "i" );
				                select.children( "option" ).each(function() {
				                	if(jQuery( this ).text().match( matcher )){
				                		this.selected = valid = true;
				                		return false;
				                	}
								});
				                return valid;
							});
			                this.value = terms.join( ", " );
			                return false;	
						} else {
							var matcher = new RegExp( "^" + jQuery.ui.autocomplete.escapeRegex( jQuery(this).val() ) + "$", "i" ),
							valid = false;
							select.children( "option" ).each(function() {
								if ( jQuery( this ).text().match( matcher ) ) {
									this.selected = valid = true;
									return false;
								}
							});
							if ( !valid ) {
								// remove invalid value, as it didn't match anything
								jQuery( this ).val( "" );
								select.val( "" );
								input.data( "autocomplete" ).term = "";
								return false;
							}
						}
					}
				},
	            focus: function() {
	            	return !multiple;
	            },

			})
			//.addClass( "ui-widget ui-widget-content ui-corner-left" )
			;

		input.data( "autocomplete" )._renderItem = function( ul, item ) {
			return jQuery( "<li></li>" )
				.data( "item.autocomplete", item )
				.append( "<a>" + item.label + "</a>" )
				.appendTo( ul );
		};
	},

	destroy: function() {
		this.input.remove();
		this.element.show();
		jQuery.Widget.prototype.destroy.call( this );
	}
});