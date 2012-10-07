/*
TurboGrafx16Collector - Mobile application to manage a collection of TurboGrafx-16 games
Copyright (C) 2010 Hugues Johnson

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software 
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package com.huguesjohnson.tg16collector;

import java.util.ArrayList;

public class MyGamesActivity extends BaseActivityTab{

	@Override
	public ArrayList<TG16Record> getGameList(){
		return(getDbHelper().getMyGames(this.getTitleFilter()));
	}

	@Override
	public ArrayList<String> getExportList(){
		return(getDbHelper().getRawData(DatabaseHelper.EXPORT_MYGAMES));
	}
}