package com.example.fixu.ui.profile

import android.app.AlertDialog
import android.content.Context

class PrivacyPolicyDialog {

    fun show(context: Context) {
        val privacyPolicy = """
            Privacy Policy for FixU
            
            Effective Date: 12-12-2024

            1. Data Collection:
               - FixU collects and processes the following personal data to enhance user experience:
                 a. User answers from diagnostic questionnaires.
                 b. App preferences (e.g., theme settings).

            2. Data Usage:
               - The collected data is used to:
                 a. Provide accurate mental health diagnostics.
                 b. Enhance application functionality and user experience.
                 c. Store user preferences for a customized experience.

            3. Data Sharing:
               - FixU does not sell or share user data with third parties, except as required by law.
               - Diagnostic results may be securely sent to cloud-based APIs for processing.

            4. Security:
               - FixU ensures user data is encrypted and stored securely.
               - We regularly update our security protocols to prevent unauthorized access.

            5. User Rights:
               - Users can request data deletion or modification by contacting our support team.
               - Users can opt-out of certain features by disabling relevant app permissions.

            6. Updates:
               - FixU reserves the right to update this Privacy Policy.
               - Users will be notified of changes via in-app notifications.

            Contact Us:
               For any concerns or questions, reach out at https://github.com/7innovations.
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle("Privacy Policy")
            .setMessage(privacyPolicy)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
