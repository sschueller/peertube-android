package net.schueller.peertube.feature_server_address.presentation.address_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.schueller.peertube.feature_server_address.domain.model.ServerAddress

@Composable
fun ServerAddressListItem(
    serverAddress: ServerAddress,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Column (
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxWidth(),
//                .clickable { onItemClick(video) }
        ) {
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = serverAddress.serverName,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!serverAddress.username.isNullOrEmpty() && !serverAddress.password.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Has Credentials",
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = serverAddress.serverHost ?: "",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            IconButton(
                onClick = onEditClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Address",
                    tint = MaterialTheme.colors.onSurface
                )
            }
            IconButton(
                onClick = onDeleteClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Address",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }

    }
}