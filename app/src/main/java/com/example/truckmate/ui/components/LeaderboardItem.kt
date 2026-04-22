package com.example.truckmate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.truckmate.data.model.User

@Composable
fun LeaderboardItem(rank: Int, user: User) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("#$rank", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.username, fontWeight = FontWeight.Bold)
                Text("${ user.totalPoints } points")
            }
        }
    }
}