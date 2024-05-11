`include "../Config.v"

module MemDualFakeBB(
    input  wire                       pMemInst_pRd_bEn,
    input  wire [`ADDR_WIDTH - 1 : 0] pMemInst_pRd_bAddr,
    output reg  [`INST_WIDTH - 1 : 0] pMemInst_pRd_bData,

    input  wire                       pMemData_pRd_bEn,
    input  wire [`ADDR_WIDTH - 1 : 0] pMemData_pRd_bAddr,
    output reg  [`INST_WIDTH - 1 : 0] pMemData_pRd_bData,
    input  wire                       pMemData_pWr_bEn,
    input  wire [`ADDR_WIDTH - 1 : 0] pMemData_pWr_bAddr,
    input  wire [`DATA_WIDTH - 1 : 0] pMemData_pWr_bData,
    input  wire                       pMemData_pWr_bMask_0,
    input  wire                       pMemData_pWr_bMask_1,
    input  wire                       pMemData_pWr_bMask_2,
    input  wire                       pMemData_pWr_bMask_3
);

    import "DPI-C" context function int unsigned readSimInstData(
        input int unsigned addr,
        input byte unsigned len);
    import "DPI-C" context function int unsigned readSimMemoryData(
        input int unsigned addr,
        input byte unsigned len);
    import "DPI-C" context function void writeSimMemoryData(
        input int unsigned addr,
        input int unsigned data,
        input byte unsigned len);

    always @(pMemInst_pRd_bAddr) begin
        if (pMemInst_pRd_bEn) begin
            pMemInst_pRd_bData = readSimInstData(pMemInst_pRd_bAddr, 4);
        end
    end

    always @(pMemInst_pRd_bAddr, pMemInst_pRd_bEn) begin
        $display("pMemInst_pRd_bEn: %d", pMemInst_pRd_bEn);
        $display("pMemInst_pRd_bAddr: %x", pMemInst_pRd_bAddr);
        $display("pMemInst_pRd_bData: %x\n", pMemInst_pRd_bData);
    end

    always @(pMemData_pRd_bAddr) begin
        if (pMemData_pRd_bEn) begin
            pMemData_pRd_bData = readSimMemoryData(pMemData_pRd_bAddr, 4);
        end
    end

    wire [3: 0] pMemData_pWr_bMask = { pMemData_pWr_bMask_0,
                                       pMemData_pWr_bMask_1,
                                       pMemData_pWr_bMask_2,
                                       pMemData_pWr_bMask_3 };
    always @(pMemData_pWr_bAddr or pMemData_pWr_bData) begin
        if (pMemData_pWr_bEn) begin
            case (pMemData_pWr_bMask)
                4'b0001: writeSimMemoryData(pMemData_pWr_bAddr,
                                            pMemData_pWr_bData,
                                            1);
                4'b0011: writeSimMemoryData(pMemData_pWr_bAddr,
                                            pMemData_pWr_bData,
                                            2);
                4'b1111: writeSimMemoryData(pMemData_pWr_bAddr,
                                            pMemData_pWr_bData,
                                            4);
                default: writeSimMemoryData(pMemData_pWr_bAddr,
                                            pMemData_pWr_bData,
                                            4);
            endcase
        end
    end

endmodule
