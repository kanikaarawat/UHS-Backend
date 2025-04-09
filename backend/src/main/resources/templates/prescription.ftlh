<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Prescription</title>
    <style>
        body {
            font-family: Helvetica, sans-serif;
            font-size: 12px;
            padding: 24px;
            background-color: #fff;
        }

        .section-label {
            margin-top: 20px;
            margin-bottom: 6px;
            font-weight: 600;
        }

        .textarea {
            background-color: #dddce2;
            padding: 12px;
            border-radius: 4px;
            min-height: 60px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 12px;
        }

        th, td {
            border: 1px solid black;
            padding: 6px;
            text-align: center;
        }

        .dosage-header th {
            font-weight: bold;
            background-color: #fff;
        }

        .signature {
            text-align: right;
            margin-top: 30px;
        }

        .signature .name {
            font-weight: 600;
        }

        .signature .title {
            font-weight: bold;
        }
    </style>
</head>
<body>

<!-- HEADER -->
<table style="width: 100%; border-collapse: collapse; margin-bottom: 10px;">
    <tr>
        <td style="width: 20%;">
            <img src="https://uhs-backend.onrender.com/assets/upes-logo.jpg" style="height: 50px;" />
        </td>
        <td style="text-align: center; font-size: 20px; font-weight: bold;">
            UPES Health Services
        </td>
        <td style="width: 20%; text-align: right; font-size: 12px;">
            <div>${time?substring(0,5)}</div>
            <div>${date}</div>
        </td>
    </tr>
</table>
<hr style="border: 0.5px solid black;"/>

<!-- PATIENT INFO -->
<table style="width: 100%; font-size: 12px; margin-top: 10px;">
    <tr>
        <td style="font-weight: bold; width: 90px;">Name:</td>
        <td style="background-color: #dddce2; padding: 6px 10px; border-radius: 4px; width: 40%;">
            ${name}
        </td>
        <td style="font-weight: bold; width: 90px;">ID:</td>
        <td style="background-color: #dddce2; padding: 6px 10px; border-radius: 4px;">
            ${id}
        </td>
    </tr>
    <tr>
        <td style="font-weight: bold;">Age:</td>
        <td style="background-color: #dddce2; padding: 6px 10px; border-radius: 4px;">
            ${age}
        </td>
        <td style="font-weight: bold;">School:</td>
        <td style="background-color: #dddce2; padding: 6px 10px; border-radius: 4px;">
            ${school}
        </td>
    </tr>
    <tr>
        <td style="font-weight: bold;">Sex:</td>
        <td style="background-color: #dddce2; padding: 6px 10px; border-radius: 4px;">
            ${sex}
        </td>
        <td style="font-weight: bold;">Residence Type:</td>
        <td style="background-color: #dddce2; padding: 6px 10px; border-radius: 4px;">
            ${residenceType}
        </td>
    </tr>
</table>

<hr style="margin-top: 12px; margin-bottom: 12px; border: 0.5px solid black;"/>

<!-- DIAGNOSIS -->
<div class="section-label">Diagnosis:</div>
<div class="textarea">${diagnosis}</div>

<!-- MEDICINE TABLE -->
<div class="section-label">Medicine:</div>
<table>
    <thead>
        <tr>
            <th>S. No.</th>
            <th>Medicine</th>
            <th colspan="3">Dosage (/day)</th>
            <th>Duration</th>
            <th>Suggestions</th>
        </tr>
        <tr class="dosage-header">
            <th></th>
            <th></th>
            <th>Morning</th>
            <th>Afternoon</th>
            <th>Evening</th>
            <th></th>
            <th></th>
        </tr>
    </thead>
    <tbody>
    <#list meds as med>
        <tr>
            <td>${med_index + 1}</td>
            <td>${med.name}</td>
            <td>${med.dosageMorning}</td>
            <td>${med.dosageAfternoon}</td>
            <td>${med.dosageEvening}</td>
            <td>${med.duration}</td>
            <td>${med.suggestion}</td>
        </tr>
    </#list>
    </tbody>
</table>

<!-- RECOMMENDATIONS -->
<div class="section-label">Recommendations:</div>
<div class="textarea">${dietaryRemarks}</div>

<!-- TESTS NEEDED -->
<div class="section-label">Tests Needed:</div>
<div class="textarea">${testNeeded}</div>

<!-- SIGNATURE -->
<div class="signature">
    <div class="name">${doctorName}</div>
    <div>(${designation})</div>
    <div class="title">Doctor</div>
</div>

</body>
</html>
